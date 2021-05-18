package com.example.heallo

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.sip.SipSession
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_first, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment

        mapFragment.getMapAsync(this)

        // Inflate the layout for this fragment

        return rootView
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setDefaultLocation()
        
        //Map Touch Event
        mMap.setOnMapClickListener { p0 ->
            val marker = MarkerOptions()
                    .position(p0)
                    .title("MapClickEvent")
                    .snippet("Save This Location")
            mMap.addMarker(marker)

            val cameraMoveToClick = CameraPosition.Builder()
                    .target(p0)
                    .zoom(12f)
                    .build()
            mMap.setOnCameraMoveListener {
                val camera = CameraUpdateFactory.newCameraPosition(cameraMoveToClick)
            }
        }

        //#1. 마커 새로찍으면 이전꺼 지워지게
        //#2. 찍은곳 위치 정보 나오게
        //#3. 적절한 zoom 찾기
        //#4. Layout 재구성
    }

    private fun setDefaultLocation(){
        val seoul = LatLng(37.5663, 126.9779)

        //마커
        var marker = MarkerOptions()
                .position(seoul)
                .title("Maker")
        mMap.addMarker(marker)

        //카메라
        val cameraOption = CameraPosition.Builder()
                .target(seoul)
                .zoom(12f)
                .build()

        var camera = CameraUpdateFactory.newCameraPosition(cameraOption)
        mMap.moveCamera(camera)
    }
}


