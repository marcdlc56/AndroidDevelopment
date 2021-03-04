package com.marcdelacruz.grumpyboy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




    }

    /** Kotlin Version, called on press of the Send button */
    fun sendMessage(view: View) {
        var editText = findViewById(R.id.editText) as EditText
        var message = editText.text.toString()
        message += " so";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}