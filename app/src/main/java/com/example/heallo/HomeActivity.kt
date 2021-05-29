package com.example.heallo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        btn_innerFacility.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)//다음 화면으로 이동하기 위한 인텐트 객체
            Log.d("Debug Lod", "Intent Error")
            startActivity(intent)
            Log.d("Debug Lod", "Intent Error2")
        }

        btn_runningRoutine.setOnClickListener {
            val intent2 = Intent(this, MainActivity::class.java)//RoutineActivity로 이동, (테스트를 위해)일시적으로 MainActivity지정
            Log.d("Debug Lod", "Intent Error")
            startActivity(intent2)
            Log.d("Debug Lod", "Intent Error2")
        }

        btn_login_home.setOnClickListener {
            val intent3 = Intent(this@HomeActivity, LoginActivity::class.java)
            Log.d("Debug Lod", "Intent Error")
            startActivity(intent3)
            Log.d("Debug Lod", "Intent Error2")
        }



    }
}