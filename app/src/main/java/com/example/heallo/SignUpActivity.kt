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
        if (su_id?.text.toString().isNullOrBlank() || su_pwd?.text.toString().isNullOrBlank()
            || su_name?.text.toString().isNullOrBlank() || su_pwd2?.text.toString().isNullOrBlank()) {
            // 각 입력값에 공백문자가 포함인 경우 (blank)/
            Toast.makeText(this@SignUpActivity, "공백문자를 입력할 수 없습니다.", Toast.LENGTH_LONG)
                .show()
        } else if(su_pwd?.text.toString() != su_pwd2?.text.toString()) {
            //비번과 비번확인란의 값이 다른 경우.
            Toast.makeText(this@SignUpActivity, "비밀번호가 다릅니다.", Toast.LENGTH_LONG)
                .show()
        }else if(su_pwd.text.toString().length in 8..20){

            // 비밀번호 8~20인 경우 정상적인 회원가입
            createUser(
                su_id?.text.toString(),
                su_pwd?.text.toString(),
                su_name?.text.toString()
            )
        } else {
            Toast.makeText(this@SignUpActivity, "비밀번호 입력 양식을 확인하세요.", Toast.LENGTH_LONG)
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