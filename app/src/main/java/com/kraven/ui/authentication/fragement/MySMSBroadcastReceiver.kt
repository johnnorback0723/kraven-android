package com.kraven.ui.authentication.fragement

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import org.greenrobot.eventbus.EventBus

class MySMSBroadcastReceiver : BroadcastReceiver() {
  //  var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action === SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    EventBus.getDefault().post(messageIntent)
                    //smsBroadcastReceiverListener!!.onSuccess(messageIntent)
                }
                //CommonStatusCodes.TIMEOUT -> smsBroadcastReceiverListener!!.onFailure()
            }
        }
    }

   /* interface SmsBroadcastReceiverListener {
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }*/
}