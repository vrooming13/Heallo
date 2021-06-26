@file:Suppress("DEPRECATION")

package com.example.heallo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var sharePreferences : SharedPreferences
    private lateinit var editor : SharedPreferences.Editor // 데이터 기록을 위한 editor

    private val SPLASH_VIEW_TIME: Long = 2000 //2초간 스플래시 화면을 보여줌 (ms)

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 분기 추가 필요 (로그인 상태 점검 후 )

        sharePreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharePreferences.edit()

        checklogin()

    }


    private fun checklogin(){

        val state = sharePreferences.getBoolean("autostate",false)
        val user = sharePreferences.getString("username","null")
        val userpwd = sharePreferences.getString("userpwd","null")


        if (state){ // true 일 경우에만 저장된 정보로 login 함수 실행.
            //loginUsers(email = user, password = userpwd)
            Log.d("test","$state")
            Log.d("test","$user")
            Log.d("test","$userpwd")

            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
        else {
            Handler().postDelayed(
                { //delay를 위한 handler
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                },
                SPLASH_VIEW_TIME,
            )
        }

    }


}