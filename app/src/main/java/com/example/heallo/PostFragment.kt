package com.example.heallo


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.volley.VolleyLog
import com.android.volley.VolleyLog.TAG
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.UploadTask.TaskSnapshot
import kotlinx.android.synthetic.main.fregment_home.*
import kotlinx.android.synthetic.main.fregment_post.*
import kotlinx.android.synthetic.main.fregment_post.view.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class PostFragment : Fragment(), OnMapReadyCallback {

    lateinit var mContext: Context

    //////Map
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var mMap: GoogleMap
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    var mLatLng: LatLng? = null
    var mAddress: String? = null

    val PERM_STORAGE = 102

    /////firebase
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var auth: FirebaseAuth? = null

    //data
    private var photouri: Uri? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()


        Places.initialize(
            requireActivity().applicationContext,
            "AIzaSyA9huPrOwes_Q9rm_8Xcs0_8aM-juUOn9Y"
        )

        Log.i("API", "${R.string.API_KEY}")

        val placesClient = Places.createClient(requireActivity())

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000 // 10???
        locationRequest.fastestInterval = 5000 // ?????? ???????????? ??????
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.fregment_post, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val searchbar =
            childFragmentManager.findFragmentById(R.id.search_posting) as AutocompleteSupportFragment

        mapFragment.getMapAsync(this)

        /////// ???????????? ////////////
        searchbar.setTypeFilter(TypeFilter.ESTABLISHMENT)
        searchbar.setLocationBias(
            RectangularBounds.newInstance(
                LatLng(-33.880490, 151.184363),
                LatLng(-33.858754, 151.229596)

            )
        )
        searchbar.setCountries("KR")

        searchbar.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
        )


        searchbar.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                mMap.clear()
                Log.i(TAG, "Place : ${p0.name},  ${p0.id}")
                Log.i(TAG, "Latlng: ${p0.latLng}")
                Log.i(TAG, "Address: ${p0.address}")
                setLocation(p0)
                mLatLng = p0.latLng
                mAddress = p0.address


            }

            override fun onError(p0: Status) {
                Log.i(TAG, "An error occurred: $p0")
            }
        })

        rootView.gallery.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERM_STORAGE)
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PERM_STORAGE)
            onActivityResult(PERM_STORAGE, RESULT_OK, intent)
        }

        rootView.write_btn.setOnClickListener {
            contentUpload(mLatLng!!)// ????????? fragment ?????? ??????.
        }


        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PERM_STORAGE -> {
                    data?.data?.let { uri ->
                        imageView.setImageURI(uri)
                        Log.i("URI", "${uri}")
                        photouri = uri
                        Log.i("URI2", "${photouri}")
                    }
                }
            }
        }
        //val uploadType = childFragmentManager.findFragmentById(R.id.imageView);
        //uploadType?.onActivityResult(requestCode, resultCode, data)

        //// ?????? ????????????
        /* if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
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
        }*/
    }

    private fun setLocation(p0: Place) {
        var mLatLng = p0.latLng
        val geo = Geocoder(mContext, Locale.KOREA)

        var marker = MarkerOptions()
            .position(mLatLng)
            .title("????????????")
        mMap.addMarker(marker)

        //?????????
        val cameraOption = CameraPosition.Builder()
            .target(mLatLng)
            .zoom(17f)
            .build()

        var camera = CameraUpdateFactory.newCameraPosition(cameraOption)
        mMap.moveCamera(camera)

        /* //???????????????
         mMap.setOnInfoWindowClickListener {
             val address = geo.getFromLocation(mLatLng!!.latitude, mLatLng.longitude, 2)
             Toast.makeText(mContext, "??????????????? : ${address[0].getAddressLine(0)}",Toast.LENGTH_LONG)
                 .show()
         }*/
    }


    @SuppressLint("MissingPermission")
    private fun addLocationListener() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    inner class MyLocationCallBack : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)


            getLocationButton?.setOnClickListener {
                val location = p0?.lastLocation
                if (getLocationButton != null) {
                    location?.run {
                        val latLng = LatLng(latitude, longitude) // ??????, ??????
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                        Log.d("MapsActivity", "??????: $latitude, ??????: $longitude")
                        var marker = MarkerOptions()
                            .position(latLng)
                            .title("????????????")
                        mMap.addMarker(marker)
                    }
                }
            }

            /*  if (removeLocationButton != null) {
                removeLocationButton.setOnClickListener {
                    removeLocationListener()
                }
            }*/
        }
    }

    private val REQUEST_ACCESS_FINE_LOCATION = 1000

    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) =
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                cancel()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } else {
            ok()
        }

    private fun showPermissionInfoDialog() {
        alert("?????? ????????? ???????????? ?????? ????????? ???????????????", "????????? ????????? ??????") {
            yesButton {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }
            noButton { }
        }.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    addLocationListener()
                } else {
                    toast("????????? ?????? ???")
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("ONRESUME", "RESUME")
        // ?????? ??????
        permissionCheck(
            cancel = { showPermissionInfoDialog() },   // ?????? ?????? ?????????
            ok = { addLocationListener() }      //    ??????????????? ?????? ????????? ??????
        )
    }

    override fun onPause() {
        super.onPause()
        removeLocationListener()    // ?????? ???????????? ?????? ????????? ?????? ?????? ?????? ??????
    }

    private fun removeLocationListener() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    fun contentUpload(latLng: LatLng) {
        val imageFileName = "JPEG_" + auth?.currentUser?.uid + "_.png"
        val storageRef = storage?.reference?.child("post")?.child(imageFileName)
        storageRef?.putFile(photouri!!)?.addOnSuccessListener {

            storageRef.downloadUrl.addOnSuccessListener {
                val contentDTO = ContentDTO()

                //????????? ??????
                contentDTO.imageUrl = it.toString()
                //????????? UID
                contentDTO.uid = auth?.currentUser?.uid
                //???????????? ??????
                contentDTO.explain = textarea.text.toString()
                //????????? ?????? ??????
                contentDTO.longtiude = latLng?.longitude

                contentDTO.latitude = latLng?.latitude
                //????????? ?????? ????????????
                contentDTO.address = mAddress
                //????????? ?????????
                contentDTO.userId = auth?.currentUser?.email
                //????????? ????????? ??????
                contentDTO.timestamp = System.currentTimeMillis()
//                var date = Date(System.currentTimeMillis())
//                Log.d("timetest","$date") // Tue Jun 08 22:13:16 GMT+09:00 2021 ?????? ??????
//                var mformat = SimpleDateFormat("yyyy-mm-dd - HH:mm:ss") //date ?????? ??????
//                var pdatetime = mformat.format(date) // ???????????? ?????? ??????


                //???????????? ???????????? ??????
                firestore?.collection("post")?.document()?.set(contentDTO)

                requireActivity().setResult(RESULT_OK)
                activity?.let {
                    val intent = Intent(context, MainActivity::class.java) // ???????????? ?????????????????? ???????????? ??????
                    startActivity(intent)
                    Toast.makeText(mContext, "???????????? ??????????????????.", Toast.LENGTH_LONG)
                        .show()
                    activity?.finish()
                }
            }?.addOnFailureListener {
                    Toast.makeText(mContext, "???????????? ?????????????????????", Toast.LENGTH_SHORT).show()
            }
        }

    }
    /*Toast.makeText(mContext, "?????? ?????????????????????", Toast.LENGTH_SHORT).show()*/

    //????????? ????????? ??? ?????? ?????? ??? ?????????(?????????)??? ????????? ?????? ??????
    //?????? ??????


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
            mLatLng = p0
            Log.i("mLatLng", "${mLatLng}")
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
            // ???????????????
            val mPosition: LatLng = it.position
            val address = geo.getFromLocation(mPosition.latitude, mPosition.longitude, 1)
            Toast.makeText(mContext, "??????????????? : ${address[0].getAddressLine(0)}", Toast.LENGTH_LONG)
                .show()
        }
        //#2. ????????? ?????? ?????? ?????????
        //#3. ????????? zoom ??????
        //#4. Layout ?????????
    }

    private fun setDefaultLocation() {
        val seoul = LatLng(37.5663, 126.9779)
        val geo = Geocoder(mContext, Locale.KOREA)

        //??????
        var marker = MarkerOptions()
            .position(seoul)
            .title("Marker")
        mMap.addMarker(marker)

        //?????????
        val cameraOption = CameraPosition.Builder()
            .target(seoul)
            .zoom(12f)
            .build()

        var camera = CameraUpdateFactory.newCameraPosition(cameraOption)
        mMap.moveCamera(camera)

        //???????????????
        mMap.setOnInfoWindowClickListener {
            val address = geo.getFromLocation(seoul.latitude, seoul.longitude, 2)
            Toast.makeText(mContext, "??????????????? : ${address[0].getAddressLine(0)}", Toast.LENGTH_LONG)
                .show()
        }
    }
}



























    /*@RequiresApi(Build.VERSION_CODES.O)
    private fun whitened(){
        val user = FirebaseAuth.getInstance()
        var uid = user.currentUser!!.uid
        var email =  user.currentUser!!.email


        val data = hashMapOf<String,Any>(
            "uid" to uid, //????????????
            "content" to textarea.text.toString(), //????????????
            "udate" to serverTimestamp(), // ???????????? ??????

            )
        val dateAndtime: LocalDateTime = LocalDateTime.now()
        val db = FirebaseFirestore.getInstance()
         db.collection("main_content").document("$email"+"$dateAndtime") // ?????? ??????  ????????? ????????? ?????? ????????? ??? ?????? ??????.
             .set(data)
             .addOnCompleteListener {

                 if(uris != null){
                    image_upload(uris!!, email.toString(), dateAndtime)
                     activity?.let {
                         val intent = Intent(context, MainActivity::class.java) // ???????????? ?????????????????? ???????????? ??????
                         startActivity(intent)
                         Toast.makeText(mContext, "???????????? ??????????????????.", Toast.LENGTH_LONG)
                             .show()
                         activity?.finish()
                     }
                 }else {
                     activity?.let {
                         val intent = Intent(context, MainActivity::class.java) // ???????????? ?????????????????? ???????????? ??????
                         startActivity(intent)
                         Toast.makeText(mContext, "???????????? ??????????????????.", Toast.LENGTH_LONG)
                             .show()
                         activity?.finish()
                     }
                 }
             }
             .addOnFailureListener { Log.d("create_fail","fail") }
    }


    private fun image_upload(uri: Uri, email:String, dateAndtime: LocalDateTime){
        val mStorageRef = FirebaseStorage.getInstance().reference
        mStorageRef.child("$email"+"$dateAndtime").putFile(uri) // images ?????? ???
    }*/



















