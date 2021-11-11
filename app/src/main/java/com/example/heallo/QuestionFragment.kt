package com.example.heallo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.heallo.databinding.FragmentQuestionBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.heallo.databinding.QuestionItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat


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
        binding?.questionwrite?.setOnClickListener {
            //fragment 변경.
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fregments_frame,QusetionWriteFragment())
                .addToBackStack(null)
                .commit()
        }
        binding?.detailviewfragmentRecyclerview?.adapter = DetailRecyclerViewAdapter()
        binding?.detailviewfragmentRecyclerview?.layoutManager = LinearLayoutManager(activity)
        return binding!!.root
    }


    inner class DetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var questionDTO = arrayListOf<QuestionDTO>()

        init {
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            // 현재 uid와 같은 문의내용의 문서만 반환
            firestore?.collection("Question")?.whereEqualTo("uid","$uid")
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFireStoreException ->
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(QuestionDTO::class.java)!!
                        questionDTO.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun getItemCount(): Int {
            return questionDTO.size
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = QuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(val binding: QuestionItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // binding 으로 변경
            var viewHolder = (holder as CustomViewHolder).binding
            //bundle 생성
            val fragment = QuestionDetailFragment()
            val bundle =Bundle()
            // 이미지의 설명과 이미지URL을 QuestionWriteFragment 넘김김
            bundle.putString("explain", questionDTO[position].explain)
            bundle.putString("useremail",questionDTO[position].userId)
            bundle.putString("postTime", questionDTO[position].timestamp.toString())
            bundle.putString("title",questionDTO[position].title)
            //UserId

            viewHolder.questionSubjectTextview.text = questionDTO[position].title
            viewHolder.questionIdTextview.text = convertTimestampToDate(questionDTO[position].timestamp)
            viewHolder.constraintLayout.setOnClickListener{
                fragment.arguments = bundle
                //프레그먼트 교체
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fregments_frame,fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
        fun convertTimestampToDate(timestamp: Long?): String? {
            //날짜 형식 지정.
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val date = sdf.format(timestamp)

            return date
        }

}
