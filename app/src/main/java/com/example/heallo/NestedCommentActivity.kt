package com.example.heallo

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heallo.databinding.ActivityNestedCommentBinding
import com.example.heallo.databinding.CommentHeaderBinding
import com.example.heallo.databinding.CommentItemBinding
import com.example.heallo.databinding.NestedCommentItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat


class NestedCommentActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null
    private var postTime : String? = null
    private var contentTime : String? = null
    private var contentexplain : String? = null
    private var useremail : String? = null
    private val binding by lazy {  ActivityNestedCommentBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  // 화면켜짐 유지
        postTime = intent.getStringExtra("postTime")
        contentTime = intent.getStringExtra("contentTime")
        contentexplain = intent.getStringExtra("contentexplain")
        useremail = intent.getStringExtra("useremail")
        binding.commentRecyclerview.adapter = CommentRecyclerviewAdapter()
        binding.commentRecyclerview.layoutManager = LinearLayoutManager(this)

        /// 작성버튼 클릭시 파이어베이스로 데이터 전송
        binding.commentBtnSend?.setOnClickListener {

            if(binding.commentEditMessage.text.toString().isNullOrEmpty()){
                Toast.makeText(this@NestedCommentActivity, "내용을 입력해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }else {
                var comment = ContentDTO.Comment()
                comment.userId = FirebaseAuth.getInstance().currentUser?.email
                comment.uid = FirebaseAuth.getInstance().currentUser?.uid
                comment.comment = binding.commentEditMessage.text.toString()
                comment.timestamp = System.currentTimeMillis()

                //post collection 안에 document 안에 comment 컬렉션 새로 생성
                FirebaseFirestore.getInstance().collection("post")
                    .document(useremail!!+"+"+postTime?.toLong())
                    .collection("comments")
                    .document(useremail!!+"+"+contentTime?.toLong())
                    .collection("recomments")
                    .document(comment.userId +"+"+comment.timestamp).set(comment)
                // 글 작성후 공백 만들기
                binding.commentEditMessage.setText("")
                hideKeyboard()
            }
        }

        /* comment_btn_cancel?.setOnClickListener {
             var intent = Intent(it.context, MainActivity::class.java)
             startActivity(intent)
         }*/
    }
    enum class contentType(val num:Int) {
        HEADER(0),CONTENT(1)
    }
    inner class CommentRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        //데이터 선언
        var comments : ArrayList<ContentDTO.Comment> = arrayListOf()

        init{
            // empty 댓글 만들기
            var comment = ContentDTO.Comment()
            comment.userId ="empty"
            comment.uid = "empty"
            comment.comment = "empty"
            comment.timestamp = System.currentTimeMillis()

            //초기 데이터 검색후 arraylist에저장.
            FirebaseFirestore.getInstance()
                .collection("post")
                .document(useremail!!+"+"+postTime?.toLong()!!)
                .collection("comments")
                .document(useremail!!+"+"+contentTime?.toLong()!!)
                .collection("recomments")
                .orderBy("timestamp")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    comments.clear()
                    if(querySnapshot == null)return@addSnapshotListener

                    for(snapshot in querySnapshot.documents!!){
                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    //인덱스 0배열에 빈데이터  출력 형식: ContentDTO.Comment
                    comments.add(0,comment)
                    notifyDataSetChanged()
                }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return  when(viewType) {
                contentType.HEADER.num -> {
                    ViewHolder2(CommentHeaderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
                }
                contentType.CONTENT.num -> {
                    ViewHolder(NestedCommentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
                }
                else -> throw IllegalArgumentException()
            }
        }
        // 뷰홀더 지정.
        inner class ViewHolder(binding: NestedCommentItemBinding): RecyclerView.ViewHolder(binding.root!!){
            val id : TextView = binding.commentIdTextview
            val comment : TextView = binding.commentContainTextview
            val del : TextView = binding.commentDelTv
        }
        inner class ViewHolder2(binding: CommentHeaderBinding): RecyclerView.ViewHolder(binding.root!!){
            var useremail = binding.textView8
            var content = binding.textView7
            var timestamp = binding.textView9
        }

        override fun getItemCount(): Int {
            return  comments.size
        }
        // 뷰바인딩
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(holder) {
                // ViewHolder 일경우 : arraylist에 있는 값의 position이 1이상일 경우
                is ViewHolder -> {
                    holder.comment.text = comments[position].comment
                    holder.id.text = comments[position].userId
                    if (FirebaseAuth.getInstance().currentUser?.email != comments[position].userId) {
                        //로그인한 유저와 작성자가 다를경우 삭제버튼 invisible
                        holder.del.visibility = View.GONE
                    }
                    holder.del.setOnClickListener {
                        Log.d("test", "삭제클릭")
                        var alertDialog = AlertDialog.Builder(this@NestedCommentActivity)
                        alertDialog.setTitle("댓글 삭제")
                        alertDialog.setMessage("댓글을 삭제하시겠습니까?")
                        alertDialog.setPositiveButton(
                            "확인"
                        ) { dialogInterface, i ->
                            // 삭제함수 실행.
                            FirebaseFirestore.getInstance().collection("post")
                                .document(useremail!!+"+"+postTime?.toLong()!!)
                                .collection("comments")
                                .document(useremail!!+"+"+contentTime?.toLong()!!)
                                .collection("recomments")
                                .document(comments[position].userId + "+" + comments[position].timestamp)
                                .delete()
                            //아이템삭제
                            comments.removeAt(position)
                            //적용
                            notifyDataSetChanged()

                        }
                        alertDialog.setNegativeButton(
                            "취소",
                            { dialogInterface, i -> dialogInterface.dismiss() })
                        alertDialog.show()

                    }
                }
                // ViewHolder 일경우 : arraylist에 있는 값의 position이 0일 경우
                is ViewHolder2 -> {
                    holder.useremail.text = useremail
                    holder.content.text = contentexplain
                }
                else -> throw IllegalArgumentException()
            }
        }

        override fun getItemViewType(position: Int): Int {
            // 아이템 뷰타입 분기. arraylist 인덱스(position)에 따라서 분기.
            return when(position) {
                0 ->  contentType.HEADER.num
                1 -> contentType.CONTENT.num
                else -> contentType.CONTENT.num
            }
        }



    }

    // 키보드 숨기기 함수 정의
    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken,0)
    }

    fun convertTimestampToDate(timestamp: Long?): String? {
        //날짜 형식 지정.
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = sdf.format(timestamp)

        return date
    }


}