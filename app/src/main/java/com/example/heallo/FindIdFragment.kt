package com.example.heallo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import com.example.heallo.databinding.FragmentFindIdBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [FindIdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FindIdFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
                val view = FragmentFindIdBinding.inflate(LayoutInflater.from(container?.context), container, false)
                setFragmentResultListener("findid") { findid, bundle ->
        //            Log.d("test","${bundle.getString("key1")}")
                    param1 = "아이디는" + bundle.getString("key1") + "입니다."
                    view.textView4.text = param1
                }

                view.button.setOnClickListener {

                    // Inflate the layout for this fragment
                    gologin_activity()
                }
                return view.root

             }

        private fun gologin_activity() {
            val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
            startActivity(intent)
            activity?.finish()
        }
}
