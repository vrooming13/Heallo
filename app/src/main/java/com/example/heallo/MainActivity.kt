package com.example.heallo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { // 초기 앱 실행시 view 생성
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // xml 디자인 보여줌
    }
}