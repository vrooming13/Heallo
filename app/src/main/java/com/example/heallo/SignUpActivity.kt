package com.example.heallo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
class SignUpActivity : AppCompatActivity() {
    //뷰가 생성되었을 때
    private var firebaseAuth: FirebaseAuth? = null
    private var input_id: EditText ?= null
    private var input_pwd: EditText? = null
    private var check_pwd: EditText? = null
    private var input_name: EditText? = null
    private var buttonJoin: Button? = null
    private var loginBtn: Button? = null

    private fun onClick() {
        // SignUpActivity 연결
        if (su_id?.text.toString().isEmpty() && su_pwd?.text.toString().isNotEmpty()
        ) {
            // 이메일과 비밀번호가 공백이 아닌 경우
            createUser(
                su_id?.text.toString(),
                su_pwd?.text.toString(),
                su_name?.text.toString()
            )
        } else {
            // 이메일과 비밀번호가 공백인 경우
            Toast.makeText(this@SignUpActivity, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG)
                .show()
        }
    }
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()
        input_id = su_id
        input_pwd = su_pwd
        check_pwd = su_pwd2
        input_name = su_name
        loginBtn = login_btn1
        buttonJoin = sign_up_btn2

        buttonJoin!!.setOnClickListener {
            onClick()
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