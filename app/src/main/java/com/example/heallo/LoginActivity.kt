package com.example.heallo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.cs.heallo.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    private var input_email: EditText? = null
    private var input_pwd: EditText? = null
    private var login_btn: Button? = null
    private var sign_up_btn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        input_email = login_id
        input_pwd = login_pwd
        sign_up_btn = sign_up_btn1

        sign_up_btn!!.setOnClickListener {
            fun onClick(v: View?) {
                // SignUpActivity 연결
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
        login_btn = login_btn1
        login_btn!!.setOnClickListener {
            fun onClick(v: View?) {
                if (input_email?.text.toString() != "" && input_pwd?.text
                        .toString() != ""
                ) {
                    loginUser(
                        input_email?.text.toString(),
                        input_pwd?.text.toString()
                    )
                } else {
                    Toast.makeText(this@LoginActivity, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show()
                }
            }
        }

        FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user: FirebaseUser? = firebaseAuth.currentUser
            if (user != null) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
            }
        }.also { firebaseAuthListener = it }
    }
    private fun loginUser(email: String?, password: String?) {
        firebaseAuth?.signInWithEmailAndPassword(email.toString(), password.toString())
            ?.addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    firebaseAuthListener?.let { firebaseAuth?.addAuthStateListener(it) }
                } else {
                    // 로그인 실패
                    Toast.makeText(
                        this@LoginActivity,
                        "아이디 또는 비밀번호가 일치하지 않습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (firebaseAuthListener != null) {
            firebaseAuth?.removeAuthStateListener(firebaseAuthListener!!)
        }
    }
}