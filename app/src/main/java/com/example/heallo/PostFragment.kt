package com.example.heallo


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.location.Geocoder

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult


import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fregment_post.*
import kotlinx.android.synthetic.main.fregment_post.view.*
import java.util.*
import android.content.Intent.ACTION_PICK as IntentACTION_PICK


class PostFragment : Fragment(), OnMapReadyCallback {

    lateinit var mContext: Context
    private lateinit var mMap: GoogleMap
    val PERM_STORAGE = 102

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

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
        return rootView
    }

    override fun onActivityResult(requestCode :Int, resultCode : Int, data:Intent?) {
        // val uploadType = childFragmentManager.findFragmentById(R.id.imageView);
        //uploadType?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            when (requestCode) {
                PERM_STORAGE -> {
                    Log.d("test2","${data?.data}")
                    data?.data?.let { uri ->
                       imageView.setImageURI(uri)
                        Log.d("test","${uri}")
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


}











