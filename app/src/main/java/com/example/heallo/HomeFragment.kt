package com.example.heallo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.heallo.databinding.FragmentHomeBinding
import com.example.heallo.databinding.ItemDetailBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton

import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(){

    private var mainView : FragmentHomeBinding?= null

    lateinit var mContext: Context
    private val AUTOCOMPLETE_REQUEST_CODE = 1


    ///Maps



    //// firebase
    var user: FirebaseUser? = null
    var firestore: FirebaseFirestore? = null
    var imagesSnapshot: ListenerRegistration? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//         mainView = inflater.inflate(R.layout.fragment_detail, container, false)
        mainView = FragmentHomeBinding.inflate(LayoutInflater.from(container?.context),container,false)
        user = FirebaseAuth.getInstance().currentUser
        firestore = FirebaseFirestore.getInstance()
        return mainView!!.root
    }

    override fun onResume() {
        super.onResume()

//        permissionCheck(
//            cancel = { showPermissionInfoDialog() },   // 권한 필요 안내창
//           // ok = { addLocationListener()}      //    주기적으로 현재 위치를 요청
//        )

        mainView?.detailviewfragmentRecyclerview?.adapter = DetailRecyclerViewAdapter()
        mainView?.detailviewfragmentRecyclerview?.layoutManager = LinearLayoutManager(activity)
    }

    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }


    inner class DetailRecyclerViewAdapter : RecyclerView.Adapter<ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO>
        var contentUidList: ArrayList<String>

        init {
            contentDTOs = ArrayList()
            contentUidList = ArrayList()
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            firestore?.collection("post")?.orderBy("timestamp", Query.Direction.DESCENDING)
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(val binding: ItemDetailBinding ) : RecyclerView.ViewHolder(binding.root)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // binding 으로 변경
            var viewHolder = (holder as CustomViewHolder).binding

            //UserId
            viewHolder.detailviewitemProfileTextview.text=contentDTOs!![position].userId

            //Image
            Glide
                .with(holder.itemView.context)
                .load(contentDTOs[position].imageUrl)
                .into(viewHolder.detailviewitemImageviewContent)

            //글 내용
            viewHolder.detailviewitemExplainTextview.text =
                contentDTOs[position].explain

            //좋아요(즐겨찾기)
            viewHolder.detailviewitemFavoritecounterTextview.text =
                "Likes  " + contentDTOs!![position].favoriteCount

            //좋아요(즐겨찾기) 이벤트
            viewHolder.detailviewitemFavoriteImageview.setOnClickListener {
                favoriteEvent(position)
            }

            if(contentDTOs[position].favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)){
                viewHolder.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_baseline_star_filld)
            }
            else {
                viewHolder.detailviewitemFavoriteImageview.setImageResource(R.drawable.ic_baseline_star_border_24)
            }

            viewHolder.detailviewitemCommentImageview.setOnClickListener{ v ->
                var intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                startActivity(intent)
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



    private val REQUEST_ACCESS_FINE_LOCATION = 1000

    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) =
        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
                cancel()
            } else{
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                    ,REQUEST_ACCESS_FINE_LOCATION)
            }
        } else {
            ok()
        }

    private fun showPermissionInfoDialog() {
        alert("위치 정보를 얻으려면 위치 권한이 필요합니다", "권한이 필요한 이유") {
            yesButton {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION)
            }
            noButton {  }
        }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                  //  addLocationListener()
                } else {
                    toast("권한이 거부 됨")
                }
                return
            }
        }
    }



    override fun onPause() {
        super.onPause()

    }
    override fun onDestroyView() { // onDestroyView 에서 binding class 인스턴스 참조를 정리해주어야 한다.
        super.onDestroyView()
        mainView = null
        }



}



