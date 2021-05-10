package com.example.heallo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        login_btn.setOnClickListener{
            val intent = Intent( this,MainActivity::class.java)
            startActivity(intent);
        }


        sign_up_btn.setOnClickListener{
            val intent2 = Intent( this,SignUpActivity::class.java)
            startActivity(intent2);
        }


    }



}