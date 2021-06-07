package com.example.heallo

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fregment_user_info.*
import kotlinx.android.synthetic.main.fregment_user_info.view.*

class UserInfoFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootview : View = inflater.inflate(R.layout.fregment_user_info, container, false)

       // rootview.info_tv.  텍스트
        rootview.pwd_rl.setOnClickListener { //비밀번호 수정
            pwd_message()
            Log.d("touch1","비번")
        }
        rootview.responsed_rl.setOnClickListener {  //작성게시물
            Log.d("touch2","작성")
        }
        rootview.fav_rl.setOnClickListener {  // 관심 게시물
            Log.d("touch3","관심")
        }
        rootview.Logout_layout.setOnClickListener {  // 로그아웃 box
           alert_message() // 로그아웃 창

            Log.d("touch4","로그아웃")
        }
        rootview.secession.setOnClickListener {  //탈퇴하기
            Log.d("touch4","탈퇴")
            delete_message()
        }

        return rootview
    }


    @SuppressLint("UseRequireInsteadOfGet")
    fun alert_message() {

        val builder =  AlertDialog.Builder(this.activity!!)
        builder.setTitle("로그아웃")
        builder.setMessage("접속중인 기기에서 로그아웃 하시겠습니까?")
        builder.setNegativeButton(
            "취소",
            null
        )
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
            FirebaseAuth.getInstance().signOut() // 로그아웃
            activity?.let{
                val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
                startActivity(intent)
                activity?.finish()

            }
        }

        builder.setCancelable(false)
        builder.show()
    }
    @SuppressLint("UseRequireInsteadOfGet")
    fun pwd_message() {
        var editTextNewPassword = EditText(activity)

            editTextNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            var alertDialog = AlertDialog.Builder(this.activity!!)
            alertDialog.setTitle("패스워드 변경")
            alertDialog.setMessage("변경하고 싶은 패스워드를 입력하세요(8~20자)")
            alertDialog.setView(editTextNewPassword)
            alertDialog.setPositiveButton(
                "변경"
            ) { dialogInterface, i ->
                if (editTextNewPassword.text.toString().length in 8..20){
                changePassword(editTextNewPassword.text.toString())} else {
                    Toast.makeText(activity, "글자수를 8~20자로 제한해 주세요.", Toast.LENGTH_LONG).show()
                }
            }
        alertDialog.setNegativeButton("취소", { dialogInterface, i -> dialogInterface.dismiss() })
            alertDialog.show()



    }

    fun changePassword(password:String){
        FirebaseAuth.getInstance().currentUser!!.updatePassword(password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(activity, "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(activity, task.exception.toString(), Toast.LENGTH_LONG).show()

            }
        }
    }
    @SuppressLint("UseRequireInsteadOfGet")
    fun delete_message() {

        val builder =  AlertDialog.Builder(this.activity!!)
        builder.setTitle("회원탈퇴")
        builder.setMessage("접속중인 계정을 탈퇴 하시겠습니까?")
        builder.setNegativeButton(
            "취소",
            null
        )
        builder.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->

             var user = FirebaseAuth.getInstance().currentUser!!
                    user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) { // 탈퇴 성공시
                            Toast.makeText(activity, "정상적으로 계정이 탈퇴되었습니다.", Toast.LENGTH_LONG).show()
                            activity?.let {
                                val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
                                startActivity(intent)
                                activity?.finish()
                            }
                    }
                }
            }



        builder.setCancelable(false)
        builder.show()
    }



}




