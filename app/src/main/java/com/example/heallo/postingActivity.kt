package com.example.heallo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class postingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting2)

        supportFragmentManager.beginTransaction()
            .replace(R.id.MapFrame, MapFragment())
            .commit()
    }


}