package com.example.heallo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations.map
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton

import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    lateinit var mContext: Context
    private lateinit var mMap: GoogleMap


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000 // 10초
        locationRequest.fastestInterval = 5000 // 최소 업데이트 시간

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fregment_home, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        return rootView
    }

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
            Toast.makeText(mContext, "도로명주소 : ${address[0].getAddressLine(0)}",Toast.LENGTH_LONG)
                .show()
        }
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
            Toast.makeText(mContext, "도로명주소 : ${address[0].getAddressLine(0)}",Toast.LENGTH_LONG)
                .show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    inner class MyLocationCallBack : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            val location = p0?.lastLocation

            location?.run{
                val latLng = LatLng(latitude, longitude) // 위도, 경도
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

                Log.d("MapsActivity", "위도: $latitude, 경도: $longitude")
                var marker = MarkerOptions()
                    .position(latLng)
                    .title("Marker")
                mMap.addMarker(marker)
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
                    addLocationListener()
                } else {
                    toast("권한이 거부 됨")
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("ONRESUME", "RESUME")
        // 권한 요청
        permissionCheck(
            cancel = { showPermissionInfoDialog() },   // 권한 필요 안내창
            ok = { addLocationListener()}      //    주기적으로 현재 위치를 요청
        )
    }

    override fun onPause() {
        super.onPause()
        removeLocationListener()    // 앱이 동작하지 않을 때에는 위치 정보 요청 제거
    }

    private fun removeLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }




}