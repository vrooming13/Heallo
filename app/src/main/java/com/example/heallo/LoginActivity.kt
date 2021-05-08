package com.example.heallo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_btn.setOnClickListener() {
            val intentmain = Intent(this, MainActivity::class.java)
            startActivity(intentmain)
        }
        
        signup.setOnClickListener(){
            val intentsign = Intent(this, SignUpActivity::class.java)
            startActivity(intentsign)
            
        }
    }
}