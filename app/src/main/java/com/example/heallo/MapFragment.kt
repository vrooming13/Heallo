package com.example.heallo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        val seoul = LatLng(37.5663, 126.9779)

        //마커
        val marker = MarkerOptions()
            .position(seoul)
            .title("Maker")
        mMap.addMarker(marker)

        //카메라
        val cameraOption = CameraPosition.Builder()
            .target(seoul)
            .zoom(12f)
            .build()

        val camera = CameraUpdateFactory.newCameraPosition(cameraOption)
        mMap.moveCamera(camera)
    }


}

