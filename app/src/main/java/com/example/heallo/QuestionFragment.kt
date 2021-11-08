package com.example.heallo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.heallo.databinding.FragmentQuestionBinding
import com.google.firebase.firestore.FirebaseFirestore


class QuestionFragment : Fragment() {
    private var firestore: FirebaseFirestore? = null
        private var binding : FragmentQuestionBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore= FirebaseFirestore.getInstance()
        binding = FragmentQuestionBinding.inflate(LayoutInflater.from(container?.context),container,false)
        return binding!!.root
    }


}