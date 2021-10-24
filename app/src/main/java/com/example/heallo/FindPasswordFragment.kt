package com.example.heallo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import com.example.heallo.databinding.FragmentFindPasswordBinding
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [FindPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FindPasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1 : String? = null
    private var param2 : String? = null
    private var firebaseAuth :FirebaseAuth? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        val view = FragmentFindPasswordBinding.inflate(LayoutInflater.from(container?.context),container,false)

        // view 이메일 정보 출력
        setFragmentResultListener("findpassword"){ findpassword, bundle ->
            Log.d("test","${bundle.getString("key2")}")
            //email
            param1 = bundle.getString("key2")
//            id
//            param2 = bundle.getString("key3")
            // 파이어베이스 비밀번호 재설정 링크 발송.
            firebaseAuth?.sendPasswordResetEmail(param1!!)
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        Toast.makeText(
                            activity,
                            "비밀번호 재설정 이메일을 전송했습니다.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            view.textView5.text = param1+"로 비밀번호 재설정 링크를 보냈습니다."
        }
        view.button.setOnClickListener {
            gologin_activity()
        }
        return view.root
    }

    private fun gologin_activity(){
        val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
        startActivity(intent)
        activity?.finish()

    }

}