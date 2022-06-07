package com.kraven.fcm


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.kraven.R
import com.kraven.core.Session
import com.kraven.di.Injector
import com.kraven.ui.activity.HomeActivity
import com.kraven.utils.ConstantApp
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import javax.inject.Inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var session: Session
    var showPush = true

    var gson: Gson? = null
    private var tag: String? = null
    private var orderId: String? = null

    override fun onCreate() {
        super.onCreate()
        Injector.INSTANCE.applicationComponent.inject(this)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("Hlink", "remoteMessage.data   = ${remoteMessage.data}")
        gson = Gson()
        try {
            val message = remoteMessage.data["message"]

            val jsonObject = JSONObject(remoteMessage.data["message"])
            tag = if (jsonObject.has(ConstantApp.PassValue.FLAG)) jsonObject.getString(ConstantApp.PassValue.FLAG) else ""
            orderId = if (jsonObject.has(ConstantApp.PassValue.ORDER_ID)) jsonObject.getString(ConstantApp.PassValue.ORDER_ID) else ""

            val notification = gson?.fromJson(message, AppFirebaseNotification::class.java)!!

            if (showPush)
                sendNotification(notification)
            EventBus.getDefault().post(notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onNewToken(token: String) {
        session.deviceId = token
    }

    private fun sendNotification(notification: AppFirebaseNotification) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(ConstantApp.PassValue.ORDER_ID, orderId)
        intent.putExtra(ConstantApp.PassValue.TAG, tag)


        val notifyPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = "0"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(notification.message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(notifyPendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel human readable title"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}