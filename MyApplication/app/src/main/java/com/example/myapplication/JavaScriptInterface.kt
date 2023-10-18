package com.example.idvonesdk

import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast

class JavaScriptInterface(private val context: Context) {

    @JavascriptInterface
    fun onIDVResult(checkStatus: String, document: String, entityId: String) {
        val resultMessage = "IDV Results: $checkStatus, $document, $entityId"
        Toast.makeText(context, resultMessage, Toast.LENGTH_LONG).show()
    }

    @JavascriptInterface
    fun onInputRequired(entityId: String, checkStatus: String) {
        // Handle input required event in the native Android application
        // Implement your handling logic here
    }

    @JavascriptInterface
    fun onError(message: String, errorStatus: String) {
        // Handle errors in the native Android application
        // Implement your error handling logic here
    }
}
