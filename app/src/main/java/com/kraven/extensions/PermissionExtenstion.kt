package com.kraven.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import com.fondesa.kpermissions.*
import com.fondesa.kpermissions.extension.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.kraven.R
import com.kraven.ui.base.BaseActivity
import com.kraven.utils.LocationManager
import com.kraven.utils.PermissionManager
import java.util.*


fun locationPermission(activity: FragmentActivity): Boolean {
    var isDone = false
    PermissionManager.Builder()
            .permission(PermissionManager.PermissionEnum.ACCESS_FINE_LOCATION)
            .askAgain(true)
            .callback(object : PermissionManager.FullCallback {
                override fun result(permissionsGranted: ArrayList<PermissionManager.PermissionEnum>,
                                    permissionsDenied: ArrayList<PermissionManager.PermissionEnum>,
                                    permissionsDeniedForever: ArrayList<PermissionManager.PermissionEnum>, permissionsAsked: ArrayList<PermissionManager.PermissionEnum>) {
                    if (permissionsGranted.size >= 0) {
                        isDone = true
                    } else if (permissionsDeniedForever.size >= 0) {

                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle(activity.getString(R.string.need_permissions))
                        builder.setMessage(activity.getString(R.string.permission_message))

                        builder.setPositiveButton(activity.getString(R.string.yes)) { dialog, _ ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", activity.packageName, null)
                            intent.data = uri
                            activity.startActivity(intent)
                            dialog.dismiss()
                        }
                        builder.setNegativeButton(activity.getString(R.string.no)) { dialog, _ ->
                            dialog.dismiss()
                            //navigator.goBack()
                        }

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }

            })
            .ask(activity)
    return isDone
}

@SuppressLint("LogNotTimber")
fun requirePermission(activity: FragmentActivity, message: String, callback: () -> Unit) {
    activity.permissionsBuilder(Manifest.permission.ACCESS_FINE_LOCATION).build().send {
        if (it.allGranted()) {
            val locationManager = LocationManager()
            locationManager.setActivity(activity as AppCompatActivity)
            locationManager.triggerLocation(object : LocationManager.LocationListener {
                override fun onLocationAvailable(latLng: LatLng) {
                    callback()
                }

                override fun onFail(status: LocationManager.LocationListener.Status) {
                    (activity as BaseActivity).showErrorMessage(status.name)
                }
            })
        } else if (it.allDenied()) {

        } else if (it.allPermanentlyDenied()) {
            val dialog = Dialog(activity, android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.dialog_info_yes_no)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)

            val textViewDialogTitle = dialog.findViewById(R.id.textViewDialogTitle) as AppCompatTextView
            textViewDialogTitle.text = dialog.context.getString(R.string.app_name)
            val textViewInfo = dialog.findViewById(R.id.textViewInfo) as AppCompatTextView
            textViewInfo.text = message
            val buttonYes = dialog.findViewById(R.id.buttonYes) as MaterialButton
            buttonYes.setOnClickListener {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
                dialog.dismiss()
                activity.finishAffinity()
            }

            val buttonNo = dialog.findViewById(R.id.buttonNo) as MaterialButton
            buttonNo.setOnClickListener {
                dialog.dismiss()
                activity.finishAffinity()
            }
            if (dialog.isShowing.not())
                dialog.show()
        } else if (it.allShouldShowRationale()) {
        } else {
        }
    }
}