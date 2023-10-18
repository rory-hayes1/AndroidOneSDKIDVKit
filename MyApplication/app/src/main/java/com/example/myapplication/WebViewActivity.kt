package com.example.idvonesdk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var uploadMessage: ValueCallback<Array<Uri>>? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var currentPhotoPath: String? = null
    private var uri: Uri? = null

    private val permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission_group.MICROPHONE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        MediaStore.ACTION_IMAGE_CAPTURE
    )
    private val PERMISSION_REQUEST_CODE = 100
    private var takePictureLauncher: ActivityResultLauncher<Uri>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                // Image captured successfully, use the saved file path (currentPhotoPath) as needed
                Log.d(TAG, "Image saved at: $currentPhotoPath")
                // The image was saved into the given Uri -> do something with it
                val msg = "Image captured successfully at : $uri"
//                Log.v(AppCompatActivity.TAG, msg)
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                filePathCallback?.onReceiveValue(arrayOf(uri!!))
            } else {
                // Image capture failed or was cancelled
                Log.d(TAG, "Image capture failed or cancelled")
            }
        }

        // Initialize WebView
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.0.0 Mobile Safari/537.36"

        // Check and request permissions
        requestPermissionsIfNecessary()

        // Load the HTML file from assets
        webView.loadUrl("https://192.168.1.105:5500/index.html")


        // Set a WebViewClient to handle links within the WebView
        webView.webViewClient = MyWebViewClient()
        webView.webChromeClient = MyWebChromeClient()
    }

    private fun requestPermissionsIfNecessary() {
        val permissionsToRequest = mutableListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        // To allow redirections
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            // Allow webview to redirect normaly. "false" indicates no override of the default behaviour
            return false
        }
        // To allow self signed https servers
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler,
            error: SslError?
        ) {
            // Handle SSL errors here
            // For a self-signed certificate, you might choose to proceed anyway
            handler.proceed()
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {

        override fun onPermissionRequest(request: PermissionRequest?) {
            val requestedResources = request!!.resources
            for (r in requestedResources) {
                if (r == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                    request.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                    break
                }
            }
        }

        override fun onPermissionRequestCanceled(request: PermissionRequest?) {
            super.onPermissionRequestCanceled(request)
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallbackParam: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
//            Log.v(AppCompatActivity.TAG, "Show a file chooser")
            filePathCallback = filePathCallbackParam

            uri = createImageFile()?.let {
                FileProvider.getUriForFile(
                    this@WebViewActivity,
                    "com.example.ffintegrationandroidapp.fileprovider",
                    it
                )
            }
            takePictureLauncher?.launch(uri);

            return true
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SS",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        return image
    }

    companion object {
        private const val TAG = "WebViewActivity"
    }
}
