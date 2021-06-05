package com.example.heallo


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fregment_post.*
import kotlinx.android.synthetic.main.fregment_post.view.*
import java.time.LocalDateTime
import java.util.*


class PostFragment : Fragment(), OnMapReadyCallback {

    lateinit var mContext: Context
    private lateinit var mMap: GoogleMap
    val PERM_STORAGE = 102
    private var firebaseAuth: FirebaseAuth? = null
    private var uris: Uri? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.fregment_post, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment


        mapFragment.getMapAsync(this)

        rootView.gallery.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PERM_STORAGE)
            val intent = Intent (Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent,PERM_STORAGE)
            onActivityResult(PERM_STORAGE, RESULT_OK,intent)
        }

        rootView.write_btn.setOnClickListener {
            this.whitened() // 성공시 fragment 전환 필요.

        }

        return rootView
    }

    override fun onActivityResult(requestCode :Int, resultCode : Int, data:Intent?) {
        // val uploadType = childFragmentManager.findFragmentById(R.id.imageView);
        //uploadType?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            when (requestCode) {
                PERM_STORAGE -> {
                    
                    data?.data?.let { uri ->

                       imageView.setImageURI(uri)
                        uris = uri
                    }
                }
            }
        }
    }




 /*
    Binding.gallery.setOnClickListener {
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),PERM_STORAGE)
        val intent = Intent (ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent,PERM_STORAGE)

        onActivityResult(requestCode = 1, resultCode = PERM_STORAGE,data =intent)
        return Binding.root
    }
  */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val geo = Geocoder(mContext, Locale.KOREA)
        setDefaultLocation()

        //Map Touch Event
        mMap.setOnMapClickListener { p0 ->
            val marker = MarkerOptions()
                .position(p0)
                .title("MapClickEvent")
                .snippet("Save This Location")
            mMap.clear()
            mMap.addMarker(marker)

            val cameraMoveToClick = CameraPosition.Builder()
                .target(p0)
                .zoom(15f)
                .build()
            mMap.setOnCameraMoveListener {
                val camera = CameraUpdateFactory.newCameraPosition(cameraMoveToClick)
            }
        }

        mMap.setOnInfoWindowClickListener {
            // 도로명주소
            val mPosition : LatLng = it.position
            val address = geo.getFromLocation(mPosition.latitude, mPosition.longitude, 1)
            Toast.makeText(mContext, "도로명주소 : ${address[0].getAddressLine(0)}", Toast.LENGTH_LONG)
                .show()
        }
        //#2. 찍은곳 위치 정보 나오게
        //#3. 적절한 zoom 찾기
        //#4. Layout 재구성
    }

    private fun setDefaultLocation(){
        val seoul = LatLng(37.5663, 126.9779)
        val geo = Geocoder(mContext, Locale.KOREA)

        //마커
        var marker = MarkerOptions()
            .position(seoul)
            .title("Marker")
        mMap.addMarker(marker)

        //카메라
        val cameraOption = CameraPosition.Builder()
            .target(seoul)
            .zoom(12f)
            .build()

        var camera = CameraUpdateFactory.newCameraPosition(cameraOption)
        mMap.moveCamera(camera)

        //도로명주소
        mMap.setOnInfoWindowClickListener {
            val address = geo.getFromLocation(seoul.latitude, seoul.longitude, 2)
            Toast.makeText(mContext, "도로명주소 : ${address[0].getAddressLine(0)}", Toast.LENGTH_LONG)
                .show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun whitened(){
        val user = FirebaseAuth.getInstance()
        var uid = user.currentUser!!.uid
        var email =  user.currentUser!!.email


        val data = hashMapOf<String,Any>(
            "uid" to uid, //올린사람
            "content" to textarea.text.toString(), //내용등록
            "udate" to serverTimestamp(), // 서버시간 등록
            )
        val dateAndtime: LocalDateTime = LocalDateTime.now()
        val db = FirebaseFirestore.getInstance()
         db.collection("main_content").document("$email"+"$dateAndtime") // 경로 등록  리뷰도 컬렉션 경로 재지정 및 생성 필요.
             .set(data)
             .addOnCompleteListener {
                 Log.d("create","success")
                 Log.d("check","$uris")
                 if(uris != null){
                    image_upload(uris!!,email!!,dateAndtime!!)
                 }
             }
             .addOnFailureListener { Log.d("create_fail","fail") }





    }


private fun image_upload(uri: Uri, email:String, dateAndtime: LocalDateTime){
    val mStorageRef = FirebaseStorage.getInstance().reference
    mStorageRef.child("$email"+"$dateAndtime").putFile(uri) // images 파일 명
}

}














