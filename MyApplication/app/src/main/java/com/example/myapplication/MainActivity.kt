package com.example.idvonesdk

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MachineTokenFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load WebViewActivity
        val webViewIntent = Intent(this, WebViewActivity::class.java)
        startActivity(webViewIntent)
        finish()
    }

    private lateinit var tokenTextView: TextView

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Initialize your TextView
//        tokenTextView = findViewById(R.id.tokenTextView)
//
//        // Call the function to obtain the machine token using a coroutine
//        GlobalScope.launch(Dispatchers.Main) {
//            val machineToken = fetchMachineToken()
//
//            // Display the machine token in your TextView
//            tokenTextView.text = "Machine Token: $machineToken"
//        }
//    }
}
