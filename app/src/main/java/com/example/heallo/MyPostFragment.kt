package com.example.heallo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heallo.databinding.FragmentMyPostBinding
import com.example.heallo.databinding.MypostItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat


class MyPostFragment : Fragment() {
    private var binding : FragmentMyPostBinding? = null
    private var firestore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(LayoutInflater.from(container?.context),container,false)
        firestore = FirebaseFirestore.getInstance()
        binding?.detailviewfragmentRecyclerview?.adapter = DetailRecyclerViewAdapter()
        binding?.detailviewfragmentRecyclerview?.layoutManager = LinearLayoutManager(activity)

        return binding!!.root

    }

    inner class DetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs = arrayListOf<ContentDTO>()

        init {
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            //현재 로그인 유저와 동일한 uid의 게시물만 반환
            firestore?.collection("post")
                ?.whereEqualTo("uid","$uid")
                ?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.addSnapshotListener { querySnapshot, firebaseFireStoreException ->
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)!!
                        contentDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = MypostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(val binding: MypostItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // binding 으로 변경
            var viewHolder = (holder as CustomViewHolder).binding
            //bundle 생성
            val fragment = PostedPostFragment()
            val bundle =Bundle()
            // 이미지의 설명과 이미지URL을 QuestionWriteFragment 넘김김
            bundle.putString("explain", contentDTOs[position].explain)
            bundle.putString("imageUrl", contentDTOs[position].imageUrl)
            bundle.putString("useremail",contentDTOs[position].userId)
            bundle.putString("postTime", contentDTOs[position].timestamp.toString())
            bundle.putString("location",contentDTOs[position].address)
            //UserId

            viewHolder.questionSubjectTextview.text = contentDTOs[position].address
            viewHolder.questionIdTextview.text = convertTimestampToDate(contentDTOs[position].timestamp)
            viewHolder.constraintLayout.setOnClickListener{
                fragment.arguments = bundle
                //프레그먼트 교체
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fregments_frame,fragment)
                    .addToBackStack(null)
                    .commit()
            }
            viewHolder.imageView2.setOnClickListener {
                var alertDialog = AlertDialog.Builder(requireActivity())
                alertDialog.setTitle("게시물 삭제")
                alertDialog.setMessage("게시물을 삭제하시겠습니까?")
                alertDialog.setPositiveButton(
                    "확인"
                ) { dialogInterface, i ->
                    // 삭제함수 실행.
                    FirebaseFirestore.getInstance().collection("post")
                        .document( "${contentDTOs[position].userId}" + "+" +"${contentDTOs[position].timestamp}")
                        .delete()

                    //fragment 변경.
                    requireActivity().supportFragmentManager.popBackStack()
                }
                alertDialog.setNegativeButton(
                    "취소",
                    { dialogInterface, i -> dialogInterface.dismiss() })
                alertDialog.show()
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