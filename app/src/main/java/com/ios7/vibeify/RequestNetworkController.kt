package com.ios7.vibeify

import com.google.gson.Gson
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class RequestNetworkController private constructor() {
    private var client: OkHttpClient? = null

    private fun getClient(): OkHttpClient {
        if (client == null) {
            val builder = OkHttpClient.Builder()

            try {
                val trustAllCerts =
                    arrayOf<TrustManager>(
                        object : X509TrustManager {
                            @Throws(CertificateException::class)
                            override fun checkClientTrusted(
                                chain: Array<X509Certificate>,
                                authType: String,
                            ) {
                            }

                            @Throws(CertificateException::class)
                            override fun checkServerTrusted(
                                chain: Array<X509Certificate>,
                                authType: String,
                            ) {
                            }

                            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
                        },
                    )

                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.connectTimeout(SOCKET_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                builder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                builder.writeTimeout(READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                builder.hostnameVerifier(
                    HostnameVerifier { _: String?, _: SSLSession? -> true },
                )
            } catch (_: Exception) {
            }

            client = builder.build()
        }

        return client!!
    }

    fun execute(
        requestNetwork: RequestNetwork,
        method: String,
        url: String?,
        tag: String,
        requestListener: RequestNetwork.RequestListener?,
    ) {
        val reqBuilder = Request.Builder()
        val headerBuilder = Headers.Builder()

        if (requestNetwork.getHeaders().isNotEmpty()) {
            val headers = requestNetwork.getHeaders()
            for ((key, value) in headers) {
                headerBuilder.add(key, value.toString())
            }
        }

        try {
            if (requestNetwork.getRequestType() == REQUEST_PARAM) {
                if (method == GET) {
                    val httpBuilder =
                        url?.toHttpUrlOrNull()?.newBuilder()
                            ?: throw NullPointerException("unexpected url: $url")

                    if (requestNetwork.getParams().isNotEmpty()) {
                        val params = requestNetwork.getParams()
                        for ((key, value) in params) {
                            httpBuilder.addQueryParameter(key, value.toString())
                        }
                    }

                    reqBuilder.url(httpBuilder.build()).headers(headerBuilder.build()).get()
                } else {
                    val formBuilder = FormBody.Builder()
                    if (requestNetwork.getParams().isNotEmpty()) {
                        val params = requestNetwork.getParams()
                        for ((key, value) in params) {
                            formBuilder.add(key, value.toString())
                        }
                    }

                    val reqBody = formBuilder.build()
                    reqBuilder.url(url ?: "").headers(headerBuilder.build()).method(method, reqBody)
                }
            } else {
                val reqBody =
                    Gson()
                        .toJson(requestNetwork.getParams())
                        .toRequestBody("application/json".toMediaTypeOrNull())

                if (method == GET) {
                    reqBuilder.url(url ?: "").headers(headerBuilder.build()).get()
                } else {
                    reqBuilder.url(url ?: "").headers(headerBuilder.build()).method(method, reqBody)
                }
            }

            val request = reqBuilder.build()

            getClient().newCall(request).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        requestNetwork.getActivity().runOnUiThread {
                            requestListener?.onErrorResponse(tag, e.message ?: "Unknown error")
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string()?.trim().orEmpty()
                        val headers = response.headers
                        val map = hashMapOf<String, Any>()
                        for (name in headers.names()) {
                            map[name] = headers[name] ?: "null"
                        }

                        requestNetwork.getActivity().runOnUiThread {
                            requestListener?.onResponse(tag, responseBody, map)
                        }
                    }
                },
            )
        } catch (e: Exception) {
            requestListener?.onErrorResponse(tag, e.message ?: "Unknown error")
        }
    }

    companion object {
        const val GET = "GET"
        const val POST = "POST"
        const val PUT = "PUT"
        const val DELETE = "DELETE"

        const val REQUEST_PARAM = 0
        const val REQUEST_BODY = 1

        private const val SOCKET_TIMEOUT = 15000
        private const val READ_TIMEOUT = 25000

        @Volatile
        private var instance: RequestNetworkController? = null

        @JvmStatic
        fun getInstance(): RequestNetworkController {
            return instance ?: synchronized(this) {
                instance ?: RequestNetworkController().also { instance = it }
            }
        }
    }
}
