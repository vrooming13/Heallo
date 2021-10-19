package com.example.heallo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.heallo.databinding.FragmentPostedPostBinding



class PostedPostFragment : Fragment() {
    private var contentview : FragmentPostedPostBinding? = null
    var explain: String? = null
    var imageUrl: String? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        contentview = FragmentPostedPostBinding.inflate(LayoutInflater.from(container?.context),container,false)
        explain = arguments?.getString("explain")
        imageUrl = arguments?.getString("imageUrl")
        //클릭한 이미지의 설명과 이미지 주소를 저장한 변수

        println("$explain")
        println("$imageUrl")
        //확인을 위한 출력
        Glide.with(this)
            .load(imageUrl)
            .into(contentview!!.postDetailImage)

        contentview!!.postDetailText.setText("$explain")
        return contentview!!.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        contentview = null
    }



}