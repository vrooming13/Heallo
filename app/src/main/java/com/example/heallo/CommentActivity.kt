package com.example.heallo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heallo.databinding.ActivityCommentBinding
import com.example.heallo.databinding.CommentItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text


class CommentActivity : AppCompatActivity() {

    private var contentUid : String? = null
    private var contentexplain : String? = null
    private var useremail : String? = null
    private val binding by lazy {  ActivityCommentBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  // 화면켜짐 유지
        contentUid = intent.getStringExtra("contentUid")
        contentexplain = intent.getStringExtra("contentexplain")
        useremail = intent.getStringExtra("useremail")
        binding.commentRecyclerview.adapter = CommentRecyclerviewAdapter()
        binding.commentRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.textView7.text = contentexplain
        binding.textView8.text = useremail
        /// 작성버튼 클릭시 파이어베이스로 데이터 전송
        binding.commentBtnSend?.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = binding.commentEditMessage.text.toString()
            comment.timestamp = System.currentTimeMillis()

            //post collection 안에 document 안에 comment 컬렉션 새로 생성
            FirebaseFirestore.getInstance().collection("post")
                .document(contentUid!!)
                .collection("comments")
                .document(comment.userId+"+"+comment.timestamp).set(comment)

            binding.commentEditMessage.setText("")
        }

       /* comment_btn_cancel?.setOnClickListener {
            var intent = Intent(it.context, MainActivity::class.java)
            startActivity(intent)
        }*/
    }

    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<CommentRecyclerviewAdapter.ViewHolder>() {

        var comments : ArrayList<ContentDTO.Comment> = arrayListOf()

        init{
            FirebaseFirestore.getInstance()
                .collection("post")
                .document(contentUid!!)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if(querySnapshot == null)return@addSnapshotListener

                    for(snapshot in querySnapshot.documents!!){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val comment_itemview = CommentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            val view = comment_itemview.root
            return ViewHolder(comment_itemview)
        }

        inner class ViewHolder(binding: CommentItemBinding): RecyclerView.ViewHolder(binding.root!!){
            val id : TextView = binding.commentIdTextview
            val comment : TextView = binding.commentContainTextview

        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


            Log.d("test","${comments[position].comment}")
            holder.comment.text  = comments[position].comment
            holder.id.text  = comments[position].userId

        }



    }


}