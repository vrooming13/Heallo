@file:Suppress("DEPRECATION")

package com.example.heallo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.example.heallo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {



    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    private var input_email: EditText? = null
    private var input_pwd: EditText? = null
    lateinit var sharePreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor // 데이터 기록을 위한 editor
    private var autobutton : Boolean = false
    private var lastTimeBackPressed : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(view.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  // 화면켜짐 유지
        firebaseAuth = FirebaseAuth.getInstance() // 초기 시작시 null 값임
        input_email = view.loginId
        input_pwd = view.loginPwd

        sharePreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharePreferences.edit()

        view.container.setOnClickListener {
            hideKeyboard()
        }

        view.switch1.setOnCheckedChangeListener { _, isChecked->
            autobutton = isChecked //true , false
        }


        view.loginBtn1!!.setOnClickListener {
                if (input_email?.text.toString() != "" && input_pwd?.text
                        .toString() != ""
                ) {
                    if (autobutton){ //자동 로그인 체크 시 저장 후 로그인

                        editor.putBoolean("autostate", true)
                        editor.apply()
                        editor.putString("username", input_email?.text.toString())
                        editor.putString("userpwd", input_pwd?.text.toString())
                        editor.commit()


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

        view.signUpBtn1.setOnClickListener {
            // SignUpActivity 연결

            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
        view.textTouchView.setOnClickListener {
            val intent = Intent(this@LoginActivity, FindActivity::class.java)
            startActivity(intent)
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


    // 1.5초내 뒤로가기 두번 = 종료
    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500){
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this,"'뒤로' 버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_LONG).show() }
        else {
            ActivityCompat.finishAffinity(this)
            System.runFinalization()
            System.exit(0)
        }
    }


    override fun onStop() {
        super.onStop()
        if (firebaseAuthListener != null) {
            firebaseAuth?.removeAuthStateListener(firebaseAuthListener!!)
        }
    }

}