package com.example.heallo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

// 메인액티비티
class SignUpActivity : AppCompatActivity() {
    //뷰가 생성되었을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //그릴 xml 뷰 파일을 연결 시켜준다. 즉, 설정한다. r.layout = res.layout
        setContentView(R.layout.activity_sign_up)
        val sign_up_btn2: Button = findViewById(R.id.sign_up_btn2)
        sign_up_btn2.setOnClickListener { //회원가입 버튼 클릭시
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}