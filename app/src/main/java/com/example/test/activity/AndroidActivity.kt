package com.example.test.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.test.R

class AndroidActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android)
    }
}