package com.kraven.core

import android.Manifest

import java.io.File

/**
 * Created by hlink21 on 27/4/16.
 */
class  Common {


    companion object {
        var IS_LOGIN:String = "ISLOGIN"
        val LOCATION:String ="LOCATION"
        val PLACE:String = "PLACE"
        val ADDRESS_LAT_LONG :String = "address_lat_long"
        val ERROR: String="ERROR"
        val ERROR_TYPE:String= "ERROR"
        val IMAGE_PATH: String="IMAGE_PATH"
        val PROBLEM_KEY="PROBLEM_KEY"
        val DATABASE_ARRAY="DATABASE_ARRAY"
        val ISSHOW="ISSHOw"
        val NET_ERROR="NET_ERROR"
        const val VEHICLE_TYPE_SCOOTER = "Scooter"
        const val VEHICLE_TYPE_CAR = "Car"
        val UPDATE_LOCATION_CHANNEL_ID = "updateLocationChannelId"
        val UPDATE_LOCATION_CHANNEL_NAME = "Update Location"
        val UPDATE_LOCATION_GROUP_ID = "udpateLocationGroupId"
        val UPDATE_LOCATION_GROUP_NAME = "Udpate Location Group"

        val ID="ID"
        val VENDORTYPE="vendor type"
        val APP_DIRECTORY = "YSP"
    }

    object Actions{
        const val START_SERVICE = "com.kraven.start_service"
        const val STOP_SERVICE = "com.kraven.stop_service"
    }

    val IMAGE_URI = "image_uri"
    val VIDEO_URI = "video_uri"
    val SHARED_PREF_NAME = "application"
    val ACTIVITY_FIRST_PAGE = "FirstPage"
    val CURRENT_USER = "current_user"
    val USER_BUNDLE = "UserBundle"
    val USER_JSON = "UserJSON"


    val APP_DIRECTORY = "YSP"
    val SNAP_DIRECTORY = APP_DIRECTORY + File.separator + "Snap"

    val REQUEST_PERMISSION = 1
    val REQUEST_CAMERA_PERMISSION = 1
    val REQUEST_GALLERY_PERMISSION = 2

    val PERMISSIONS_GALLERY = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

    val PERMISSIONS_RECORD_VIDEO = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)

    val IS_LOGIN = "is_login"

    val TAP_TO_LOAD_LIMIT = 4
    val STORYBOARD_LOAD_LIMIT = 7
    val MESSAGE = "message"
    val DISTANCE = "Distance"
    val THEME_COLOR = "theme_color"
    var IS_ON_SERVICE = "is_on_service"
    var REQUEST_CODE_10=10
    var currentScreen: SCREEN? = null
    val ADDRESS="Address"
    val ID="ID"
    val VENDORTYPE="vendor type"


    object RequestCode {
        val REQUEST_TAKE_PHOTO = 1
        val RESULT_LOAD_IMAGE = 2
        val REQUEST_IMAGE_AND_VIDEO = 3
        val REQUEST_FROM_CAMERA = 4
        val REQUEST_TO_FINISH = 5
        val REQUEST = 10
        val REQUEST_TRIM_VIDEO = 11
        val CROP_IMAGE_ACTIVITY_REQUEST_CODE = 203

    }

    object ResultCode {
        val IMAGE_RESULT = 1
        val VIDEO_RESULT = 2
        val FINISH_ME = 444
        val MESSAGE = 10

    }

    object RegX {
        val USERNAME = "^[A-Za-z0-9_-]{3,25}$"
        val FULL_NAME = "^(?!\\s)$"

    }

    enum class SelectionModes {
        NONE, SINGLE, MULTI
    }

    enum class SCREEN {
        HOME, HISTORY, NONE
    }

}
