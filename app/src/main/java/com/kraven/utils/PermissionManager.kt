
package com.kraven.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager

import java.util.ArrayList
import java.util.Collections
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Created by JD on 22/06/16.
 */
class PermissionManager {

    private var fullCallback: FullCallback? = null
    private var simpleCallback: SimpleCallback? = null
    private var askAgainCallback: AskAgainCallback? = null
    private var smartCallback: SmartCallback? = null

    private var askAgain = false

    private var permissions: ArrayList<PermissionEnum>? = null
    private var permissionsGranted: ArrayList<PermissionEnum>? = null
    private var permissionsDenied: ArrayList<PermissionEnum>? = null
    private var permissionsDeniedForever: ArrayList<PermissionEnum>? = null
    private var permissionToAsk: ArrayList<PermissionEnum>? = null

    private var key = KEY_PERMISSION


    fun permissions(permissions: ArrayList<PermissionEnum>): PermissionManager {
        this.permissions = ArrayList()
        this.permissions!!.addAll(permissions)
        return this
    }

    fun permission(permission: PermissionEnum): PermissionManager {
        this.permissions = ArrayList()
        this.permissions!!.add(permission)
        return this
    }

    fun permission(vararg permissions: PermissionEnum): PermissionManager {
        this.permissions = ArrayList()
        Collections.addAll(this.permissions!!, *permissions)
        return this
    }

    fun askAgain(askAgain: Boolean): PermissionManager {
        this.askAgain = askAgain
        return this
    }

    fun callback(fullCallback: FullCallback): PermissionManager {
        this.simpleCallback = null
        this.smartCallback = null
        this.fullCallback = fullCallback
        return this
    }

    fun callback(simpleCallback: SimpleCallback): PermissionManager {
        this.fullCallback = null
        this.smartCallback = null
        this.simpleCallback = simpleCallback
        return this
    }


    fun callback(smartCallback: SmartCallback): PermissionManager {
        this.fullCallback = null
        this.simpleCallback = null
        this.smartCallback = smartCallback
        return this
    }

    fun askAgainCallback(askAgainCallback: AskAgainCallback): PermissionManager {
        this.askAgainCallback = askAgainCallback
        return this
    }

    fun key(key: Int): PermissionManager {
        this.key = key
        return this
    }

    fun ask(activity: Activity) {
        ask(activity, null)
    }

    fun ask(fragmentX: Fragment) {
        ask(null, fragmentX)
    }

    private fun ask(activity: Activity?, fragmentX: Fragment?) {
        initArray()
        val permissionToAsk = permissionToAsk(activity, fragmentX)
        if (permissionToAsk.isEmpty()) {
            showResult()
        } else {
            if (activity != null) {
                ActivityCompat.requestPermissions(activity, permissionToAsk, key)
            } else fragmentX?.requestPermissions(permissionToAsk, key)
        }
    }

    private fun permissionToAsk(activity: Activity?, fragmentX: Fragment?): Array<String> {
        val permissionToAsk = ArrayList<String>()
        for (permission in permissions!!) {
            var isGranted = false
            if (activity != null) {
                isGranted = isGranted(activity, permission)
            }else if (fragmentX != null) {
                isGranted = isGranted(fragmentX.requireActivity(), permission)
            }
            if (!isGranted) {
                permissionToAsk.add(permission.toString())
            } else {
                permissionsGranted!!.add(permission)
            }
        }
        return permissionToAsk.toTypedArray()
    }

    private fun initArray() {
        this.permissionsGranted = ArrayList()
        this.permissionsDenied = ArrayList()
        this.permissionsDeniedForever = ArrayList()
        this.permissionToAsk = ArrayList()
    }


    private fun showResult() {
        if (simpleCallback != null)
            simpleCallback!!.result(permissionToAsk!!.size == 0 || permissionToAsk!!.size == permissionsGranted!!.size)
        if (fullCallback != null)
            fullCallback!!.result(permissionsGranted!!, permissionsDenied!!, permissionsDeniedForever!!, permissions!!)
        if (smartCallback != null)
            smartCallback!!.result(permissionToAsk!!.size == 0 || permissionToAsk!!.size == permissionsGranted!!.size, !permissionsDeniedForever!!.isEmpty())
        instance = null
    }

    companion object {

        fun isGranted(context: Context, permission: PermissionEnum): Boolean {
            return ContextCompat.checkSelfPermission(context, permission.toString()) == PackageManager.PERMISSION_GRANTED
        }


        const val KEY_PERMISSION = 100
        private var instance: PermissionManager? = null

        fun Builder(): PermissionManager {
            if (instance == null) {
                instance = PermissionManager()
            }
            return instance as PermissionManager
        }

        fun handleResult(activity: Activity, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            handleResult(activity, null, null, requestCode, permissions, grantResults)
        }


        fun handleResult(fragmentX: Fragment, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            handleResult(null, fragmentX, null, requestCode, permissions, grantResults)
        }

        private fun fromManifestPermission(value: String): PermissionEnum {
            for (permissionEnum in PermissionEnum.values()) {
                if (value.equals(permissionEnum.permission, ignoreCase = true)) {
                    return permissionEnum
                }
            }
            return PermissionEnum.NULL
        }


        @Deprecated("")
        fun handleResult(fragment: android.app.Fragment, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            handleResult(null, null, fragment, requestCode, permissions, grantResults)
        }

        private fun handleResult(activity: Activity?, fragmentX: Fragment?, fragment: android.app.Fragment?, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            if (instance == null) return
            if (requestCode == instance!!.key) {
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        instance!!.permissionsGranted!!.add(fromManifestPermission(permissions[i]))
                    } else {
                        var permissionsDeniedForever = false
                        if (activity != null) {
                            permissionsDeniedForever = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])
                        }  else if (fragmentX != null) {
                            permissionsDeniedForever = fragmentX.shouldShowRequestPermissionRationale(permissions[i])
                        }
                        if (!permissionsDeniedForever) {
                            instance!!.permissionsDeniedForever!!.add(fromManifestPermission(permissions[i]))
                        }
                        instance!!.permissionsDenied!!.add(fromManifestPermission(permissions[i]))
                        instance!!.permissionToAsk!!.add(fromManifestPermission(permissions[i]))
                    }
                }
                if (instance!!.permissionToAsk!!.size != 0 && instance!!.askAgain) {
                    instance!!.askAgain = false
                    if (instance!!.askAgainCallback != null && instance!!.permissionsDeniedForever!!.size != instance!!.permissionsDenied!!.size) {
                        instance!!.askAgainCallback!!.showRequestPermission(object : AskAgainCallback.UserResponse {
                            override fun result(askAgain: Boolean) {
                                if (askAgain) {
                                    instance!!.ask(activity, fragmentX)
                                } else {
                                    instance!!.showResult()
                                }
                            }
                        })
                    } else {
                        instance!!.ask(activity, fragmentX)
                    }
                } else {
                    instance!!.showResult()
                }
            }
        }
    }

    interface SmartCallback {
        fun result(allPermissionsGranted: Boolean, somePermissionsDeniedForever: Boolean)
    }

    interface SimpleCallback {
        fun result(allPermissionsGranted: Boolean)
    }

    interface FullCallback {
        fun result(permissionsGranted: ArrayList<PermissionEnum>, permissionsDenied: ArrayList<PermissionEnum>, permissionsDeniedForever: ArrayList<PermissionEnum>, permissionsAsked: ArrayList<PermissionEnum>)
    }

    interface AskAgainCallback {
        fun showRequestPermission(response: UserResponse)
        interface UserResponse {
            fun result(askAgain: Boolean)
        }
    }

    @SuppressLint("InlinedApi")
    enum class PermissionEnum constructor(val permission: String) {
        BODY_SENSORS(Manifest.permission.BODY_SENSORS),
        READ_CALENDAR(Manifest.permission.READ_CALENDAR),
        WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR),
        READ_CONTACTS(Manifest.permission.READ_CONTACTS),
        WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS),
        GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS),
        READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE),
        WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
        ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
        RECORD_AUDIO(Manifest.permission.RECORD_AUDIO),
        READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE),
        CALL_PHONE(Manifest.permission.CALL_PHONE),
        READ_CALL_LOG(Manifest.permission.READ_CALL_LOG),
        WRITE_CALL_LOG(Manifest.permission.WRITE_CALL_LOG),
        ADD_VOICEMAIL(Manifest.permission.ADD_VOICEMAIL),
        USE_SIP(Manifest.permission.USE_SIP),
        PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS),
        CAMERA(Manifest.permission.CAMERA),
        SEND_SMS(Manifest.permission.SEND_SMS),
        RECEIVE_SMS(Manifest.permission.RECEIVE_SMS),
        READ_SMS(Manifest.permission.READ_SMS),
        RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH),
        RECEIVE_MMS(Manifest.permission.RECEIVE_MMS),

        GROUP_CALENDAR(Manifest.permission_group.CALENDAR),
        GROUP_CAMERA(Manifest.permission_group.CAMERA),
        GROUP_CONTACTS(Manifest.permission_group.CONTACTS),
        GROUP_LOCATION(Manifest.permission_group.LOCATION),
        GROUP_MICROPHONE(Manifest.permission_group.MICROPHONE),
        GROUP_PHONE(Manifest.permission_group.PHONE),
        GROUP_SENSORS(Manifest.permission_group.SENSORS),
        GROUP_SMS(Manifest.permission_group.SMS),
        GROUP_STORAGE(Manifest.permission_group.STORAGE),

        NULL("");

        override fun toString(): String {
            return permission
        }
    }

}