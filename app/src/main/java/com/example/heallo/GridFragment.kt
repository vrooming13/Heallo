package com.example.heallo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.heallo.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.fragment_grid.view.*
import java.util.ArrayList

class GridFragment : Fragment() {

    var mainView: View? = null
    var imagesSnapshot  : ListenerRegistration? = null

    //Firebase
    var auth: FirebaseAuth? = null
    var firestore : FirebaseFirestore?= null

    var uid : String?= null
    var currentUserUid: String?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainView = inflater.inflate(R.layout.fragment_grid, container, false)

        //firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        currentUserUid = auth?.currentUser?.uid

        return mainView
    }

    override fun onResume() {
        super.onResume()
        mainView?.gridfragment_recyclerview?.adapter = GridFragmentRecyclerViewAdatper()
        mainView?.gridfragment_recyclerview?.layoutManager = GridLayoutManager(activity, 3)
    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }


    inner class GridFragmentRecyclerViewAdatper : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO>
        var contentUidList: MutableMap<String, Boolean>
        var currentid = auth?.currentUser?.uid

        init {
            contentDTOs = ArrayList()
            contentUidList = HashMap()

            // 내가 즐겨찾기한 사진
            val favorRef = firestore?.collection("post")
            favorRef?.whereEqualTo("favorites", true)?.get()
                ?.addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("Fav", "${document.id} => ${document.data.get("favorites")}")
                    }
                }
                ?.addOnFailureListener { exception ->
                    Log.w("Fav2", "Error getting documents: ", exception)
                }

            imagesSnapshot = firestore?.collection("post")?.whereEqualTo("uid", uid)?.
            addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot?.documents!!) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            //현재 사이즈 뷰 화면 크기의 가로 크기의 1/3값을 가지고 오기
            val width = resources.displayMetrics.widthPixels / 3

            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)

            return CustomViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var imageView = (holder as CustomViewHolder).imageView

            Glide.with(holder.itemView.context)
                    .load(contentDTOs[position].imageUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(imageView)

            imageView.setOnClickListener {
                val fragment = PostedPostFragment()
                //교체할 프레그먼트
                val bundle = Bundle()

                bundle.putString("explain", contentDTOs[position].explain)
                bundle.putString("imageUrl", contentDTOs[position].imageUrl)
                // 이미지의 설명과 이미지URL을 PostedPostFragment로 넘김김

               fragment.arguments = bundle
                activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.fregments_frame, fragment)
                        //프레임 레아웃 id
                        .commit()



            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }
}
