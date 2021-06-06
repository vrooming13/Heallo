package com.example.heallo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fregment_posted_post.*


class PostedPostFragment : Fragment() {

    var explain: String? = null
    var imageUrl: String? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        explain = arguments?.getString("explain")
        imageUrl = arguments?.getString("imageUrl")
        //클릭한 이미지의 설명과 이미지 주소를 저장한 변수

        println("$explain")
        println("$imageUrl")
        //확인을 위한 출력

        return inflater.inflate(R.layout.fregment_posted_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(imageUrl)
            .into(post_detail_image)

        post_detail_text.setText("$explain")
    }
}