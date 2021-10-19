package com.example.heallo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heallo.databinding.ActivityCommentBinding
import com.example.heallo.databinding.CommentItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class CommentActivity : AppCompatActivity() {

    var contentUid : String?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(view.root)

        contentUid = intent.getStringExtra("contentUid")

        view.commentRecyclerview.adapter = CommentRecyclerviewAdapter()
        view.commentRecyclerview.layoutManager = LinearLayoutManager(this)

        /// 작성버튼 클릭시 파이어베이스로 데이터 전송
        view.commentBtnSend?.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            comment.uid = FirebaseAuth.getInstance().currentUser?.uid
            comment.comment = view.commentEditMessage.text.toString()
            comment.timestamp = System.currentTimeMillis()

            //post collection 안에 document 안에 comment 컬렉션 새로 생성
            FirebaseFirestore.getInstance().collection("post")
                .document(contentUid!!)
                .collection("comments")
                .document().set(comment)

            view.commentEditMessage.setText("")
        }

       /* comment_btn_cancel?.setOnClickListener {
            var intent = Intent(it.context, MainActivity::class.java)
            startActivity(intent)
        }*/
    }

    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var comment_itemview = CommentItemBinding.inflate(layoutInflater, parent,false)
            return CustomViewHolder(comment_itemview.root)
        }

        private inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val view = CommentItemBinding.inflate(layoutInflater)
            view.commentContainTextview.text = comments[position].comment
            view.commentIdTextview.text = comments[position].userId

        }



    }


}