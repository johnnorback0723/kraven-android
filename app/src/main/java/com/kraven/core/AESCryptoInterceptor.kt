package com.kraven.core

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Android Developer on 29/11/17.
 */
@Singleton
class AESCryptoInterceptor @Inject
constructor(private val aes: AES) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val plainQueryParameters = request.url.queryParameterNames
        var httpUrl = request.url
        // Check Query Parameters and encrypt
        if (plainQueryParameters.isNotEmpty()) {
            val httpUrlBuilder = httpUrl.newBuilder()
            for (i in plainQueryParameters.indices) {
                val name = httpUrl.queryParameterName(i)
                val value = httpUrl.queryParameterValue(i)
                httpUrlBuilder.setQueryParameter(name, aes.encrypt(value))
            }
            httpUrl = httpUrlBuilder.build()
        }

        // Get Header for encryption
        val apiKey = request.headers[Session.API_KEY]
        val token = request.headers[Session.USER_SESSION]

        val newRequest: Request
        val requestBuilder = request.newBuilder()

        // Check if any body and encrypt
        val requestBody = request.body

        if (requestBody?.contentType() != null) {
            // bypass multipart parameters for encryption
            val isMultipart = !requestBody.contentType()!!.type.equals("multipart", ignoreCase = true)
            val bodyPlainText = if (isMultipart) transformInputStream(bodyToString(requestBody)) else bodyToString(requestBody)

            if (bodyPlainText != null) {
                requestBuilder
                        .post(
                            bodyPlainText.toRequestBody("text/plain".toMediaTypeOrNull())
                        )
            }
        }
        // Build the final request
        newRequest = requestBuilder.url(httpUrl)
            .header(Session.API_KEY, aes.encrypt(apiKey)!!)
            .header(Session.USER_SESSION, aes.encrypt(token)!!)
            .build()

        // execute the request
        val proceed = chain.proceed(newRequest)
        // get the response body and decrypt it.
        val cipherBody = proceed.body!!.string()

        // if (cipherBody != null && cipherBody.contains("html"))

        val plainBody = aes.decrypt(cipherBody)

        // create new Response with plaint text body for further process
        return proceed.newBuilder()
                .body(plainBody!!.trim { it <= ' ' }
                    .toResponseBody("text/json".toMediaTypeOrNull()))
                .build()

    }

    private fun bodyToString(request: RequestBody?): String? {

        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return null
            return buffer.readUtf8()
        } catch (e: IOException) {
            return null
        }

    }

    private fun transformInputStream(inputStream: String?): String? {
        return aes.encrypt(inputStream)
    }
}