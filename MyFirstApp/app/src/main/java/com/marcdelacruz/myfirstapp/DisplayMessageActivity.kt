package com.marcdelacruz.myfirstapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DisplayMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_display_message)

        val messsage = intent.getStringExtra(EXTRA_MESSAGE)
        val textview = findViewById<TextView>(R.id.textView).apply {
            text = messsage



        }
    }
}