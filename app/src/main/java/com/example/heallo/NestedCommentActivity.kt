//package com.example.heallo
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.view.WindowManager
//import android.widget.TextView
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.heallo.databinding.ActivityCommentBinding
//import com.example.heallo.databinding.CommentHeaderBinding
//import com.example.heallo.databinding.CommentItemBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import java.text.SimpleDateFormat
//
//class NestedCommentActivity : AppCompatActivity() {
//
//        private var contentUid : String? = null
//        private var contentexplain : String? = null
//        private var useremail : String? = null
//        private val binding by lazy {  ActivityCommentBinding.inflate(layoutInflater)}
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//
//            setContentView(binding.root)
//            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  // 화면켜짐 유지
//            contentUid = intent.getStringExtra("contentUid")
//            contentexplain = intent.getStringExtra("contentexplain")
//            useremail = intent.getStringExtra("useremail")
//            binding.commentRecyclerview.adapter = CommentRecyclerviewAdapter()
//            binding.commentRecyclerview.layoutManager = LinearLayoutManager(this)
//
//            /// 작성버튼 클릭시 파이어베이스로 데이터 전송
//            binding.commentBtnSend?.setOnClickListener {
//                var comment = ContentDTO.Comment()
//                comment.userId = FirebaseAuth.getInstance().currentUser?.email
//                comment.uid = FirebaseAuth.getInstance().currentUser?.uid
//                comment.comment = binding.commentEditMessage.text.toString()
//                comment.timestamp = System.currentTimeMillis()
//
//                //post collection 안에 document 안에 comment 컬렉션 새로 생성
//                FirebaseFirestore.getInstance().collection("post")
//                    .document(contentUid!!)
//                    .collection("comments")
//                    .document(comment.userId+"+"+comment.timestamp).set(comment)
//
//                binding.commentEditMessage.setText("")
//            }
//
//            /* comment_btn_cancel?.setOnClickListener {
//                 var intent = Intent(it.context, MainActivity::class.java)
//                 startActivity(intent)
//             }*/
//        }
//        enum class contentType(val num:Int) {
//            HEADER(0),CONTENT(1)
//        }
//        inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//
//            var comments : ArrayList<ContentDTO.Comment> = arrayListOf()
//
//            init{
//                FirebaseFirestore.getInstance()
//                    .collection("post")
//                    .document(contentUid!!)
//                    .collection("comments")
//                    .orderBy("timestamp")
//                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                        comments.clear()
//                        if(querySnapshot == null)return@addSnapshotListener
//
//                        for(snapshot in querySnapshot.documents!!){
//                            comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
//                        }
//                        notifyDataSetChanged()
//                    }
//            }
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//                return  when(viewType) {
//                    contentType.HEADER.num -> {
//                        ViewHolder2(CommentHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
//                    }
//                    contentType.CONTENT.num -> {
//                        ViewHolder(CommentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
//                    }
//                    else -> throw IllegalArgumentException()
//                }
//            }
//            // 뷰홀더 지정.
//            inner class ViewHolder(binding: CommentItemBinding): RecyclerView.ViewHolder(binding.root!!){
//                val id : TextView = binding.commentIdTextview
//                val comment : TextView = binding.commentContainTextview
//            }
//            inner class ViewHolder2(binding: CommentHeaderBinding): RecyclerView.ViewHolder(binding.root!!){
//                var useremail = binding.textView8
//                var content = binding.textView7
//                var timestamp = binding.textView9
//            }
//
//            override fun getItemCount(): Int {
//                return comments.size
//            }
//            // 뷰바인딩
//            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//                when(holder) {
//                    is ViewHolder -> {
//                        holder.comment.text = comments[position].comment
//                        holder.id.text = comments[position].userId
//                    }
//
//                    is ViewHolder2 -> {
//                        holder.useremail.text = useremail
//                        holder.content.text = contentexplain
//                        holder.timestamp.text = convertTimestampToDate(comments[position]?.timestamp)
//                    }
//                    else -> throw IllegalArgumentException()
//                }
//            }
//
//            override fun getItemViewType(position: Int): Int {
//                return when(position) {
//                    0 -> contentType.HEADER.num
//                    1 -> contentType.CONTENT.num
//                    else -> contentType.CONTENT.num
//                }
//            }
//
//
//
//        }
//
//        fun convertTimestampToDate(timestamp: Long?): String? {
//            //날짜 형식 지정.
//            val sdf = SimpleDateFormat("yyyy-MM-dd")
//            val date = sdf.format(timestamp)
//
//            return date
//        }
//
//
//
//}