package com.example.heallo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.heallo.databinding.FragmentFindBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class FindFragment : Fragment() {
       private  var firestore: FirebaseFirestore? = null
       private var findidFragment = FindIdFragment()
       private var findPasswordFragment = FindPasswordFragment()
       // 전역변수 선언
       private var name : EditText? =null
       private var email : EditText? =null
       private var name2 : EditText? =null
       private var return1 : String ?=null
       private var return2 : Boolean ?= false


    override fun onCreate(savedInstanceState: Bundle?) {
        firestore= FirebaseFirestore.getInstance()

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = FragmentFindBinding.inflate(LayoutInflater.from(container?.context),container,false)
        // 전역변수 값 연결.
        name = view.inputNickname
        email = view.inputEmail
        name2 = view.inputNickname2

        // 컨테이너 (배경)클릭 시 키보드 숨김/
        view.container.setOnClickListener {
//            Log.d("button","click to container button")
            hideKeyboard()

        }
        //아이디 찾기 버튼 클릭이벤트
        view.idFind.setOnClickListener{
            hideKeyboard()
            // 아이디 찾기 함수 실행.
            findid()
//            Log.d("button","click id find button")

        }

        //비밀번호 찾기 버튼 클릭이벤트
        view.passwordFind.setOnClickListener {
            hideKeyboard()
            //비밀번호 찾기 함수 실행.
            findpassword()
//            Log.d("button","click password find button")


        }

        return view.root
    }

    private fun hideKeyboard(){
        val inputManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken,0
        )
    }
    //아이디 찾기 함수 : findid() 정의
      private fun findid(){
//        Log.d("click","${name?.text.toString()}")
//        Log.d("click","${name2?.text.toString()}")
//        Log.d("click","${email?.text.toString()}")

       firestore?.collection("user")
           ?.whereEqualTo("name","${name?.text.toString()}")
           ?.get()
           ?.addOnCompleteListener { task->
               if(!task.result?.isEmpty!!){
                   for (document in task.result!!) {
                       //전역변수 return1에 결과 id 저장.
                        return1 = document.data["email"].toString()
                       setFragmentResult("findid", bundleOf("key1" to return1))
                       replacetobackstackFragment(findidFragment)
                   }
               } else //전역변수 return1에 결과 id 저장.
               {
                   Toast.makeText(
                       activity,
                       "존재하지 않는 사용자 이릅입니다.",
                       Toast.LENGTH_SHORT
                   )
                       .show()
               }
           }
    }

    //비밀번호 찾기(재정의) 함수 : findpassword() 정의
    private fun findpassword(){
        firestore // 쿼리결과1_이메일 검사
            ?.collection("user") //컬렉션
            ?.whereEqualTo("email","${email?.text.toString()}") // 이메일 검색
            ?.get()
            ?.addOnCompleteListener() {     task ->
                // 결과가 비어있으면 true 리턴 , 결과가 있으면 false -> ! 이용해서 반전.
                    if(!task.result?.isEmpty!!) {
                        firestore // 쿼리결과2 _이름 검사
                            ?.collection("user") //컬렉션
                            ?.whereEqualTo("name", "${name2?.text.toString()}") // 이름 검색
                            ?.get()
                            ?.addOnCompleteListener() { task ->
                                if(!task.result?.isEmpty!!){
                                    // 결과값 있음. 비밀번호 재설정 페이지로 이동. (email/name2 전달)

                                        for (document in task.result!!){
                                            // firestore "name" 필드 검색결과 리턴 document에서 email 필드 데이터와 입력 이메일이 일치할 경우만 1명의 유저 비밀번호 변경
                                            if (document.data["email"]=="${email?.text.toString()}"){

                                                setFragmentResult("findpassword", bundleOf("key2" to email?.text.toString(),"key3" to name2?.text.toString()))
                                                replacetobackstackFragment(findPasswordFragment)
                                            } else {
                                                Toast.makeText(
                                                    activity,
                                                    "존재하지 않는 사용자 정보입니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }


                                } else { //이메일 o, 이름 x
                                    Toast.makeText(
                                        activity,
                                        "존재하지 않는 이름 정보입니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else { // 이메일 데이터가 없을 경우 경고창.
                        firestore // 쿼리결과2 _이름 검사
                            ?.collection("user") //컬렉션
                            ?.whereEqualTo("name", "${name2?.text.toString()}") // 이름 검색
                            ?.get()
                            ?.addOnCompleteListener() { task ->
                                if(!task.result?.isEmpty!!){
                                    // 결과값 있음. 이메일 x, 이름 o
                                    Toast.makeText(
                                        activity,
                                        "존재하지 않는 id 정보입니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else { //아무것도 중복되지 않는 경우.데이터 없음 경고창.
                                    Toast.makeText(
                                        activity,
                                        "존재하지 않는 사용자 정보입니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
            }

    }

    // add + backstack 을 이용하면 뒤로가기시 이전 프레그먼트로 이동 가능.
    private fun replacetobackstackFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container,fragment) //위치지정
            .addToBackStack(null)
            transaction.commit()
        }
    }



}