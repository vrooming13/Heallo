package com.example.heallo

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.heallo.databinding.ActivitySignUpBinding


import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class SignUpActivity : AppCompatActivity() {
    //뷰가 생성되었을 때
    private var firebaseAuth: FirebaseAuth? = null
    private var input_id: EditText ?= null
    private var input_pwd: EditText? = null
    private var check_pwd: EditText? = null
    private var input_name: EditText? = null
    private var buttonJoin: Button? = null
    private var loginBtn: Button? = null


     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ActivitySignUpBinding.inflate(layoutInflater,container,false)
        setContentView(view.root)
         window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  // 화면켜짐 유지
        firebaseAuth = FirebaseAuth.getInstance()
        input_id = view.suId
        input_pwd = view.suPwd
        check_pwd = view.suPwd2
        input_name = view.suName
        buttonJoin = view.signUpBtn2

        buttonJoin!!.setOnClickListener {
            onClick()
        }
    }

    private fun onClick() {
        val view = ActivitySignUpBinding.inflate(layoutInflater,container,false)
        // SignUpActivity 연결
        if (view.suId?.text.toString().isNullOrBlank() || view.suPwd?.text.toString().isNullOrBlank()
            ||view.suName?.text.toString().isNullOrBlank() ||view.suPwd2?.text.toString().isNullOrBlank()) {
            // 각 입력값에 공백문자가 포함인 경우 (blank)/
            Toast.makeText(this@SignUpActivity, "공백문자를 입력할 수 없습니다.", Toast.LENGTH_LONG)
                .show()
        } else if(view.suPwd?.text.toString() != view.suPwd2?.text.toString()) {
            //비번과 비번확인란의 값이 다른 경우.
            Toast.makeText(this@SignUpActivity, "비밀번호가 다릅니다.", Toast.LENGTH_LONG)
                .show()
        }else if(view.suPwd.text.toString().length in 8..20){

            // 비밀번호 8~20인 경우 정상적인 회원가입
            createUser(
                view.suId?.text.toString(),
                view.suPwd?.text.toString(),
                view.suName?.text.toString()
            )
        } else {
            // 이메일과 비밀번호가 공백인 경우
            Toast.makeText(this@SignUpActivity, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun createUser(email: String, password: String, name: String) {
        firebaseAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공시
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // 계정이 중복된 경우
                    Toast.makeText(this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}