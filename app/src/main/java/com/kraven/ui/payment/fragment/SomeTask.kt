package com.kraven.ui.payment.fragment

import android.content.Context
import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by cesarferreira on 22/05/14.
 */
class SomeTask(url: String, callback: OnEventListener<String>) : AsyncTask<String, Void, String>() {

    private val mCallBack: OnEventListener<String>?

    private var mException: Exception? = null


    init {
        mCallBack = callback

    }

    override fun doInBackground(vararg strings: String): String? {

        try {
            val mURL = URL(strings[0])

            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "GET"


                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    return response.toString()
                }
            }

        } catch (e: Exception) {
            mException = e
        }

        return null
    }


    override fun onPostExecute(result: String) {
        if (mCallBack != null) {
            if (mException == null) {
                mCallBack.onSuccess(result)
            } else {
                mCallBack.onFailure(mException)
            }
        }
    }
}