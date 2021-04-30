package com.example.heallo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { // 초기 앱 실행시 view 생성
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // xml 디자인 보여줌

        val login_btn: Button =findViewById(R.id.login_btn)
        login_btn.setOnClickListener{
            val intent = Intent( this,MainActivity::class.java)
            startActivity(intent);
        }

        val sign_up_btn: Button =findViewById(R.id.sign_up_btn)
        login_btn.setOnClickListener{
            val intent = Intent( this,SignUpActivity::class.java)
            startActivity(intent);
        }
    }

}