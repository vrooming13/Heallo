package com.example.heallo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager

import com.example.heallo.databinding.FragmentQusetionWriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class QusetionWriteFragment : Fragment() {
    private var binding : FragmentQusetionWriteBinding? = null
    private var firestore: FirebaseFirestore? = null
    private var firebaseAuth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //파이어베이스 getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        
        binding = FragmentQusetionWriteBinding.inflate(LayoutInflater.from(container?.context),container,false)
        //뒤로가기
        binding?.textCancleTv?.setOnClickListener {
           requireActivity().supportFragmentManager.popBackStack()
        }

        binding?.sendButton?.setOnClickListener {
            if(!binding?.title?.text.toString().isNullOrEmpty() && !binding?.title?.text.toString().isNullOrEmpty()){
            QeustionUpload()
            } else {
                Toast.makeText(context, "제목, 내용을 입력해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return binding!!.root
    }

    private fun QeustionUpload(){

        var question = QuestionDTO()
        //값 대입
        question?.uid = firebaseAuth?.uid
        question?.userId = firebaseAuth?.currentUser?.email
        question?.title = binding?.title?.text.toString()
        question?.explain = binding?.questionExplain?.text.toString()
        question?.timestamp = System.currentTimeMillis()

        firestore?.collection("Question")
            ?.document("Question" + "+" + "${firebaseAuth?.currentUser?.email}" + "+" + "${question?.timestamp}")
            ?.set(question!!)
        Toast.makeText(requireActivity(), "문의글을 등록했습니다.", Toast.LENGTH_LONG)
            .show()
        requireActivity().supportFragmentManager.popBackStack()

    }

}