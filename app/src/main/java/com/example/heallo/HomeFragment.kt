package com.example.heallo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
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
import com.android.volley.VolleyLog.TAG
import com.example.heallo.R.string.API_KEY
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.fregment_home.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton

import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    lateinit var mContext: Context
    private lateinit var mMap: GoogleMap
    private val AUTOCOMPLETE_REQUEST_CODE = 1


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(requireActivity().applicationContext, "AIzaSyA9huPrOwes_Q9rm_8Xcs0_8aM-juUOn9Y")

        Log.i("API", "${API_KEY}")

        val placesClient = Places.createClient(requireActivity())

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
        val searchbar = childFragmentManager.findFragmentById(R.id.search) as AutocompleteSupportFragment

        mapFragment.getMapAsync(this)

        /////// 위치검색 ////////////
        searchbar.setTypeFilter(TypeFilter.ESTABLISHMENT)
        searchbar.setLocationBias(
            RectangularBounds.newInstance(
                LatLng(-33.880490, 151.184363),
                LatLng(-33.858754, 151.229596)

            )
        )

        searchbar.setCountries("KR")

        searchbar.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))


        searchbar.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place) {
                mMap.clear()
                Log.i(TAG, "Place : ${p0.name},  ${p0.id}")
                Log.i(TAG, "Latlng: ${p0.latLng}")
                setLocation(p0)
            }

            override fun onError(p0: Status) {
                Log.i(TAG, "An error occurred: $p0")
            }
        })


        return rootView
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place2: ${place.name}, ${place.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage.toString())
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
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

    private fun setLocation(p0:Place){

        var mLatLng= p0.latLng
        val geo = Geocoder(mContext, Locale.KOREA)

        var marker = MarkerOptions()
            .position(mLatLng)
            .title("검색위치")
        mMap.addMarker(marker)

        //카메라
        val cameraOption = CameraPosition.Builder()
            .target(mLatLng)
            .zoom(17f)
            .build()

        var camera = CameraUpdateFactory.newCameraPosition(cameraOption)
        mMap.moveCamera(camera)

       /* //도로명주소
        mMap.setOnInfoWindowClickListener {
            val address = geo.getFromLocation(mLatLng!!.latitude, mLatLng.longitude, 2)
            Toast.makeText(mContext, "도로명주소 : ${address[0].getAddressLine(0)}",Toast.LENGTH_LONG)
                .show()
        }*/
    }


    @SuppressLint("MissingPermission")
    private fun addLocationListener(){
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    inner class MyLocationCallBack : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            if (getLocationButton != null) {
                getLocationButton.setOnClickListener {
                    val location = p0?.lastLocation
                    if (getLocationButton != null) {
                        location?.run {
                            val latLng = LatLng(latitude, longitude) // 위도, 경도
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))

                            Log.d("MapsActivity", "위도: $latitude, 경도: $longitude")
                            var marker = MarkerOptions()
                                .position(latLng)
                                .title("현재위치")
                            mMap.addMarker(marker)
                        }
                    }
                }
            }

            if (removeLocationButton != null) {
                removeLocationButton.setOnClickListener {
                    removeLocationListener()
                }
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