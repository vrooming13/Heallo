package com.example.heallo

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.heallo.databinding.ActivitySignUpBinding


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.textColor


class SignUpActivity : AppCompatActivity() {
    //뷰가 생성되었을 때
    private var view : ActivitySignUpBinding? =null
    private var firebaseAuth: FirebaseAuth? = null
    private var input_id: EditText ?= null
    private var input_pwd: EditText? = null
    private var check_pwd: EditText? = null
    private var input_name: EditText? = null
    private var buttonJoin: Button? = null
    private var id_check_btn: Boolean? = false // 중복검사 체킁 유무 : 이름(닉네임),이메일 중복시 false,아닐시 true
    var firestore: FirebaseFirestore? = null

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         view = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(view?.root)
         window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  // 화면켜짐 유지
        firebaseAuth = FirebaseAuth.getInstance()
        input_id = view?.suId
        input_pwd = view?.suPwd
        check_pwd = view?.suPwd2
        input_name = view?.suName
        buttonJoin = view?.signUpBtn2

        firestore= FirebaseFirestore.getInstance() // 파이어스토어 저장 인스턴스 저장.

         view?.container?.setOnClickListener {
             hideKeyboard() // 공백 터치시 키보드 숨기기
         }
        view?.idCheckBtn?.setOnClickListener {
            if(input_id?.text.toString().isNullOrBlank() || input_name?.text.toString().isNullOrBlank()){
                Toast.makeText(this, "값을 입력한 후 중복 검사를 진행해 주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                checkUser(input_id!!.text.toString(), input_name!!.text.toString())
            }
        }
        buttonJoin!!.setOnClickListener {
            if(id_check_btn == true){
                onClick() // 클릭시 회원가입 진행.
            } else {
                Toast.makeText(this, "이메일,이름 중복 검사를 진행해 주세요.", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }
    // 키보드 숨기기 함수 정의
    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken,0)
    }

    // 공백,널 검사 후 아닐 시 정상적인 회원가입 함수 실행.
    private fun onClick() {
        // SignUpActivity 연결
        if (input_id?.text.toString().isNullOrBlank() || input_name?.text.toString().isNullOrBlank()
            ||input_pwd?.text.toString().isNullOrBlank() ||check_pwd?.text.toString().isNullOrBlank()) {
            // 각 입력값에 공백문자가 포함인 경우 (blank)/


            Toast.makeText(this@SignUpActivity, "공백문자를 입력할 수 없습니다.", Toast.LENGTH_LONG)
                .show()
        } else if(input_pwd?.text.toString() != check_pwd?.text.toString()) {
            //비번과 비번확인란의 값이 다른 경우.
            Toast.makeText(this@SignUpActivity, "비밀번호가 다릅니다.", Toast.LENGTH_LONG)
                .show()
        }else if(input_pwd?.text.toString().length in 8..20){

            // 비밀번호 8~20인 경우 정상적인 회원가입
            createUser(
                input_id?.text.toString(),
                input_pwd?.text.toString(),
                input_name?.text.toString()
            )
        } else {
            // 이메일과 비밀번호가 공백인 경우
            Toast.makeText(this@SignUpActivity, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG)
                .show()
        }
    }


    // 클릭시 회원가입 함수 실행.
    private fun createUser(email: String, password: String, name: String) {

        firebaseAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this
            ) { task ->
                // 회원가입 성공시
                if (task.isSuccessful) {

                    // 유저정보 firestore 저장.
                    updateUI(firebaseAuth?.currentUser,name, null.toString())
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // 계정이 중복된 경우
                    Toast.makeText(this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT)
                        .show()

                }
            }
    }


    //firestore에 유저정보 저장.
    private fun updateUI(uid: FirebaseUser?, name:String, photoUrl:String) {

        var user = UserDTO()
        user.uid = uid?.uid
        user.email = uid?.email
        user.name = name
        user.photoUrl = photoUrl

        firestore?.collection("user")?.document("${uid?.email}")?.set(user)
    }

    // 중복검사 함수.
    private fun checkUser(email:String,name:String){
        //firestore 쿼리문 이용 닉네임 이메일 동일한 것만 검색.
        firestore // 쿼리결과1
           ?.collection("user") //컬렉션
           ?.whereEqualTo("email","${email}") // 이메일 검색
           ?.get()
           ?.addOnCompleteListener(this) { task ->
               // 결과가 비어있으면 true 리턴 , 결과가 있으면 false -> ! 이용해서 반전.
                if(!task.result?.isEmpty!!) {
                      for (document in task.result!!) {
                          if (document.data["email"] == "$email") {
//                              Log.d("test", "${document.data["email"]}")
//                              Log.d("test", "$email")
                              Toast.makeText(this, "${email}은 이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT)
                                  .show()
                          }
                      }

                 } else { // 이메일 데이터가 중복되지 않는 경우 실행.
                    firestore // 쿼리결과2
                        ?.collection("user") //컬렉션
                        ?.whereEqualTo("name", "${name}") // 이름 검색
                        ?.get()
                        ?.addOnCompleteListener() { task ->
                           if(!task.result?.isEmpty!!){
                                for (document in task.result!!) {
                                    if (document.data["name"] == name) {
//                                        Log.d("test", "${document.data["name"]}")
//                                        Log.d("test", "$name")
                                        Toast.makeText(
                                            this,
                                            "${name}은 이미 존재하는 이름입니다.",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }
                            } else { //아무것도 중복되지 않는 경우. id_check_btn true로 변경.
                               Toast.makeText(
                                   this,
                                   "사용가능한 이름,이메일 입니다.",
                                   Toast.LENGTH_SHORT
                               ).show()

                               // 중복체크버튼 true 변경. 중복확인 글자변경 + 키보드 숨기기.
                               view?.idCheckBtn?.setTextColor(Color.parseColor("#996699"))
                               view?.idCheckBtn?.setTypeface(null,Typeface.BOLD)
                               id_check_btn = true
                               hideKeyboard()

                           }
                        }
                 }
           }
    }
}