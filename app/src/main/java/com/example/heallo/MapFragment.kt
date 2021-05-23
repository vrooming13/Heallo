package com.example.heallo

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_first.*

class MapFragment : Fragment(), OnMapReadyCallback {

    lateinit var mContext: Context
    private lateinit var mMap: GoogleMap
    private var mLocationManager: LocationManager? = null
    private var mLocationListener: LocationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is postingActivity)
            mContext = context
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mLocationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager?
        mLocationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                var lat = 0.0
                var lng = 0.0
                if (p0 != null) {
                    lat = p0.latitude
                    lng = p0.longitude
                    Log.d(
                        "나의 위치",
                        "Lat: $lat, lon: $lng"
                    )
                }
                var currentLocation = LatLng(lat, lng)
                mMap!!
                    .addMarker(
                        MarkerOptions()
                            .position(currentLocation)
                            .title("현재위치")
                    )

                mMap!!
                    .animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

       val getLocationButton: ImageButton = current_location

       getLocationButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000L,
                    30f,
                    mLocationListener as LocationListener
                )
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_first, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment

        mapFragment.getMapAsync(this)
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





