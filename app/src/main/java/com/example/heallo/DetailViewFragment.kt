package com.example.heallo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*


/// 맵 클러스터링 + 장소 보기
class DetailViewFragment : Fragment() {



    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null
    var mainView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = inflater.inflate(R.layout.fragment_detail, container, false)

        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()

        return mainView
    }

    override fun onResume() {
        super.onResume()

        mainView?.detailviewfragment_recyclerview?.adapter = DetailRecyclerViewAdapter()
        mainView?.detailviewfragment_recyclerview?.layoutManager = LinearLayoutManager(activity)
    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }

    inner class DetailRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val contentDTOs: ArrayList<ContentDTO>
        val contentUidList: ArrayList<String>

        init {
            contentDTOs = ArrayList()
            contentUidList = ArrayList()
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            firestore?.collection("post")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFireStoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)!!
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).itemView

            //UserId
            viewHolder.detailviewitem_profile_textview.text=contentDTOs!![position].userId

            //Image
            Glide
                .with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .into(viewHolder.detailviewitem_imageview_content)

            //글 내용
            viewHolder.detailviewitem_explain_textview.text =
                contentDTOs[position].explain

            //좋아요(즐겨찾기)
            viewHolder.detailviewitem_favoritecounter_textview.text =
                "Likes  " + contentDTOs!![position].favoriteCount

            //좋아요(즐겨찾기) 이벤트
            viewHolder.detailviewitem_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }

            if(contentDTOs[position].favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid) &&
                    contentDTOs[position].favorites.containsValue(true)){
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }
            else {
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        private fun favoriteEvent(position: Int) {
            var tsDoc =
                firestore?.collection("post")?.document(contentUidList[position])

            firestore?.runTransaction {
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val contentDTO = it.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    //좋아요 취소
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! -1
                    contentDTO?.favorites.remove(uid)
                }
                else {
                    //좋아요 적용
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! +1
                    contentDTO?.favorites[uid] = true
                }

                it.set(tsDoc, contentDTO)
            }
        }
    }


}