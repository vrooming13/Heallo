package com.example.heallo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.heallo.databinding.FragmentQuestionDetailBinding

class QuestionDetailFragment : Fragment() {
        private var binding : FragmentQuestionDetailBinding? = null
        private var explain: String? = null
        private var useremail: String? = null
        private var postTime : String? = null
        private var title : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentQuestionDetailBinding.inflate(LayoutInflater.from(container?.context),container,false)
        explain = arguments?.getString("explain")
        useremail = arguments?.getString("useremail")
        title = arguments?.getString("title")
        postTime = arguments?.getString("postTime")



        binding?.textCancleTv?.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding?.questionTitle?.text = title
        binding?.questionExplain?.text = explain

        return binding!!.root
    }

}