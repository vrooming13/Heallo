@file:Suppress("DEPRECATION")

package com.example.heallo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.editText
import org.jetbrains.anko.email
import java.util.prefs.Preferences

class LoginActivity : AppCompatActivity() {

    private val username: String? = null
    private val userpwd: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    private var input_email: EditText? = null
    private var input_pwd: EditText? = null
    lateinit var sharePreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor // 데이터 기록을 위한 editor
    private var autobutton : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance() // 초기 시작시 null 값임
        input_email = login_id
        input_pwd = login_pwd

        sharePreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharePreferences.edit()

        container.setOnClickListener {
            hideKeyboard()
        }

        switch1.setOnCheckedChangeListener { _, isChecked->
            autobutton = isChecked //true , false
        }

        sign_up_btn1!!.setOnClickListener {
                // SignUpActivity 연결
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
        }

        login_btn1!!.setOnClickListener {
                if (input_email?.text.toString() != "" && input_pwd?.text
                        .toString() != ""
                ) {
                    if (autobutton){ //자동 로그인 체크 시 저장 후 로그인

                        editor.putBoolean("autostate", true)
                        editor.apply()
                        editor.putString("username", input_email?.text.toString())
                        editor.putString("userpwd", input_pwd?.text.toString())
                        editor.commit()

                        var test = sharePreferences.getString("username","")
                        Log.d("d","$test")

                        loginUser(
                            input_email?.text.toString(),
                            input_pwd?.text.toString()
                        )

                    } else {
                        loginUser(
                            input_email?.text.toString(),
                            input_pwd?.text.toString()
                        )
                    }


                } else {
                    Toast.makeText(this@LoginActivity, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show()
                }
        }


    }
    private fun loginUser(email: String?, password: String?) {
        firebaseAuth?.signInWithEmailAndPassword(email.toString(), password.toString())
            ?.addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    firebaseAuthListener?.let { firebaseAuth?.addAuthStateListener(it) }
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
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

    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input_email?.windowToken,0)
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