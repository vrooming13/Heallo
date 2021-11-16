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
import com.example.heallo.databinding.FragmentGridBinding
import com.example.heallo.databinding.FragmentGridBinding.*
import com.example.heallo.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

import org.jetbrains.anko.padding
import java.util.ArrayList

class GridFragment : Fragment() {

    private var mainView : FragmentGridBinding?= null
    var imagesSnapshot  : ListenerRegistration? = null

    //Firebase
    var auth: FirebaseAuth? = null
    var firestore : FirebaseFirestore?= null

    var uid : String?= null
    var currentUserUid: String?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainView = FragmentGridBinding.inflate(layoutInflater,container,false)
        //firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserUid = auth?.currentUser?.uid

        return mainView!!.root
    }

    override fun onResume() {
        super.onResume()
        mainView?.gridfragmentRecyclerview?.adapter = GridFragmentRecyclerViewAdatper()
        mainView?.gridfragmentRecyclerview?.layoutManager = GridLayoutManager(activity, 3)
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

            //내가 즐겨찾기한 사진
            val favorRef = firestore?.collection("post")
            favorRef?.whereEqualTo("favorites.${currentid}", true)?.addSnapshotListener {
                    value, error ->
                contentDTOs.clear()
                if (value == null) return@addSnapshotListener
                for (snapshot in value?.documents!!) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    Log.d("Fav", "${snapshot.id} => ${snapshot.data?.get("favorites")}")
                }
                notifyDataSetChanged()
            }

            //내가 업로드한 이미지
            /*imagesSnapshot = firestore?.collection("post")?.whereEqualTo("uid", currentid)?.
            addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot?.documents!!) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                notifyDataSetChanged()
            }*/
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            //현재 사이즈 뷰 화면 크기의 가로 크기의 1/3값을 가지고 오기
            val width = resources.displayMetrics.widthPixels / 3

            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            imageView.padding = 3
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
                // 이미지의 설명과 이미지URL을 PostedPostFragment로 넘김김
                bundle.putString("explain", contentDTOs[position].explain)
                bundle.putString("imageUrl", contentDTOs[position].imageUrl)
                bundle.putString("useremail",contentDTOs[position].userId)
                bundle.putString("postTime", contentDTOs[position].timestamp.toString())
                bundle.putString("rating", contentDTOs[position].rating.toString())
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