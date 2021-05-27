package com.example.heallo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
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
import android.webkit.PermissionRequest
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.ActivityChooserView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.fragment_first.*
import java.util.*

@Suppress("UNREACHABLE_CODE")
class MapFragment : Fragment(), OnMapReadyCallback {

    lateinit var mContext: Context
    lateinit var aContext: Context
    private lateinit var mMap: GoogleMap
    private var mLocationManager: LocationManager? = null
    private var mLocationListener: LocationListener? = null
    private val PERMISSION_REQUEST_CODE : Int = 1

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
                    LocationManager.NETWORK_PROVIDER,
                    3000L,
                    30f,
                    mLocationListener as LocationListener
                )
            }

            else{
                ActivityCompat.requestPermissions(mContext as Activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_REQUEST_CODE ->{
                var allPermissionsGranted = true
                for(result in grantResults){
                    allPermissionsGranted = (result == PackageManager.PERMISSION_GRANTED)
                    if(!allPermissionsGranted) break
                }
                if(allPermissionsGranted){
                    mLocationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f,
                            mLocationListener as LocationListener)
                }
                else {
                    Toast.makeText(mContext, "위치 정보 제공 동의가 필요합니다", Toast.LENGTH_SHORT).show()
                }
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

        //search
        val searchBar = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        searchBar.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
        searchBar.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place) {
                TODO("Get info about the selected place")
                Log.i(TAG, "Place : ${p0.name}, ${p0.id}")

                val searchLocation : Place = p0
                setLocation(searchLocation)
            }

            override fun onError(p0: Status) {
                TODO("Handle the error")
                Log.i(TAG, "An error occurred : $p0")
            }

        })

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

    private fun setLocation(p1 : Place){
        val marker = MarkerOptions()
            .position(p1.latLng)
            .title("MapClickEvent")
            .snippet("Save This Location")
        mMap.clear()
        mMap.addMarker(marker)

        val cameraMoveToClick = CameraPosition.Builder()
            .target(p1.latLng)
            .zoom(20f)
            .build()
        mMap.setOnCameraMoveListener {
            val camera = CameraUpdateFactory.newCameraPosition(cameraMoveToClick)
        }
    }




}







