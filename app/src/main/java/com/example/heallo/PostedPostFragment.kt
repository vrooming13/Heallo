package com.example.heallo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.heallo.databinding.FragmentPostedPostBinding
import com.google.firebase.firestore.FirebaseFirestore


class PostedPostFragment : Fragment() {
    private var contentview : FragmentPostedPostBinding? = null
    private var explain: String? = null
    private var imageUrl: String? = null
    private var postTime : String? = null
    private var userId : String? = null
    private var location : String? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        contentview = FragmentPostedPostBinding.inflate(LayoutInflater.from(container?.context),container,false)
        explain = arguments?.getString("explain")
        imageUrl = arguments?.getString("imageUrl")
        userId = arguments?.getString("useremail")
        postTime = arguments?.getString("postTime")
        location = arguments?.getString("location")
        //클릭한 이미지의 설명과 이미지 주소를 저장한 변수

        //확인을 위한 출력
        Glide.with(this)
            .load(imageUrl)
            .into(contentview!!.postDetailImage)
        // 텍스트 내용이 없으면 글내용 및 텍스트 영역 VISIBLITY = GONE
        if(explain.isNullOrEmpty()){
            contentview!!.textView6.visibility = View.GONE
            contentview!!.postDetailText.visibility = View.GONE
        }
        //삭제이모티콘 누를경우
        contentview!!.delete.setOnClickListener {
            var alertDialog = AlertDialog.Builder(requireActivity())
            alertDialog.setTitle("게시물 삭제")
            alertDialog.setMessage("게시물을 삭제하시겠습니까?")
            alertDialog.setPositiveButton(
                "확인"
            ) { dialogInterface, i ->
                // 삭제함수 실행.
                FirebaseFirestore.getInstance().collection("post")
                    .document(userId!!+"+"+postTime?.toLong()!!)
                    .delete()

                //fragment 변경.
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fregments_frame,HomeFragment())
                    .commit()
            }
            alertDialog.setNegativeButton(
                "취소",
                { dialogInterface, i -> dialogInterface.dismiss() })
            alertDialog.show()
        }
        contentview!!.detailviewitemProfileTextview.text = userId
        contentview!!.location.text = location
        contentview!!.postDetailText.text  = explain

        return contentview!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        contentview = null
    }



}