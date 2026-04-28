package com.ios7.vibeify

import android.app.Activity

class RequestNetwork(private val activity: Activity) {
    private var params: HashMap<String, Any> = hashMapOf()
    private var headers: HashMap<String, Any> = hashMapOf()
    private var requestType = 0

    fun setHeaders(headers: HashMap<String, Any>) {
        this.headers = headers
    }

    fun setParams(params: HashMap<String, Any>, requestType: Int) {
        this.params = params
        this.requestType = requestType
    }

    fun getParams(): HashMap<String, Any> = params

    fun getHeaders(): HashMap<String, Any> = headers

    fun getActivity(): Activity = activity

    fun getRequestType(): Int = requestType

    fun startRequestNetwork(
        method: String,
        url: String?,
        tag: String,
        requestListener: RequestListener?,
    ) {
        RequestNetworkController.getInstance().execute(this, method, url, tag, requestListener)
    }

    interface RequestListener {
        fun onResponse(tag: String, response: String, responseHeaders: HashMap<String, Any>)

        fun onErrorResponse(tag: String, message: String)
    }
}
