package com.example.heallo

import ListAdapter
import ResultSearchKeyword
import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heallo.databinding.FragmentPostBinding
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton



class PostFragment : Fragment() {

    lateinit var mContext: Context

    private var rootView : FragmentPostBinding? = null
    private var mapView : net.daum.mf.map.api.MapView? =null
    //kakao map api permission
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    val PERM_STORAGE = 102

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 02817d76f79710d1febf48672cf97f49"  // REST API 키
    }

    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private val listAdapter = ListAdapter(listItems)    // 리사이클러 뷰 어댑터



    /////firebase
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var auth: FirebaseAuth? = null
    private var uLatitude : Double? = null
    private var uLongitude : Double? = null
    private var addresses : String? = null
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


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment root view 생성
         rootView = FragmentPostBinding.inflate(LayoutInflater.from(container?.context),container,false)
        // mapview 생성
         mapView = net.daum.mf.map.api.MapView(activity)

        val recyclerView: RecyclerView = rootView!!.rvList


        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = listAdapter


        //선택시 이벤트 처리
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                //좌표 지정.
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                mapView!!.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
                //rootView!!.searchView.setQuery("",false)
                // 검색 리싸이클러뷰 닫기.
                recyclerView.visibility= GONE
                // 위도경도 저장.
                uLatitude = listItems[position].y
                uLongitude = listItems[position].x
                addresses = listItems[position].address
            }
        })



        val permissionCheck = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {

                val userNowLocation: Location =
                    lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!! // 유저 현재위치 저장.
                 uLatitude = userNowLocation.latitude  // 사용자 위도
                 uLongitude = userNowLocation.longitude // 사용자 경도
                val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!) // 유저 현재 위치 저장.
                // 중심점 변경.
                mapView!!.setMapCenterPointAndZoomLevel(uNowPosition,2,true) //  kakao map 위도,경도를 통한 현재 위치 지정.
                //줌 컨트롤러

                //내 위치마커
                var defaultmaker = MapPOIItem()
                defaultmaker.apply {
                    itemName="현재 위치"
                    mapPoint = MapPoint.mapPointWithGeoCoord(uLatitude!!,uLongitude!!)
                    markerType = MapPOIItem.MarkerType.BluePin
//                    selectedMarkerType = MapPOIItem.MarkerType.CustomImage  // 클릭 시 마커 모양 (커스텀)
//                    customSelectedImageResourceId = R.drawable.이미지       // 클릭 시 커스텀 마커 이미지
//                    isCustomImageAutoscale = false      // 커스텀 마커 이미지 크기 자동 조정
//                    setCustomImageAnchor(0.5f, 1.0f)    // 마커 이미지 기준점
                }

                mapView!!.addPOIItem(defaultmaker)



            }catch(e: NullPointerException){
                Log.e("LOCATION_ERROR", e.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    Log.d("test","1")
//                    ActivityCompat.finishAffinity(this)
                }else{
                    Log.d("test","2")

//                    ActivityCompat.finishAffinity(this)
                }
                Log.d("test","3")

//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                System.exit(0)
            }
        }else{
            Toast.makeText(activity, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )
        }
//        서치뷰 검색처리.
        rootView!!.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // 검색 버튼 누를 때 호출
            override fun onQueryTextSubmit(query: String?): Boolean {
//                searchKeyword("$query")
                return true
            }

            // 검색창에서 글자가 변경이 일어날 때마다 호출
            override fun onQueryTextChange(newText: String?): Boolean {

                //입력 글자 상태에 따라서 recyclerView hide & show
                if(!newText!!.isNullOrEmpty()){
                    rootView!!.rvList.setVisibility(View.VISIBLE);
                }else{
                    rootView!!.rvList.setVisibility(View.GONE);
                }

                // 검색 함수 실행.
                searchKeyword("$newText")

                return true
            }

        })


        rootView!!.mapView.addView(mapView)



        rootView!!.gallery.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERM_STORAGE)
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PERM_STORAGE)
            onActivityResult(PERM_STORAGE, RESULT_OK, intent)
        }

        rootView!!.writeBtn.setOnClickListener {
            Log.d("test","Click wbt")
           contentUpload()// 성공시 fragment 전환 필요.
        }







        return rootView!!.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PERM_STORAGE -> {
                    data?.data?.let { uri ->
                        rootView?.imageView?.setImageURI(uri)
                        Log.i("URI", "$uri")
                        photouri = uri
                        Log.i("URI2", "$photouri")
                    }
                }
            }
        }
    }




    private fun contentUpload() {
        var content = ContentDTO()
        Log.d("test","$photouri")
        Log.d("test","$addresses")
        Log.d("test","$uLatitude")
        Log.d("test","$uLongitude")

        if(photouri != null ){
            val imageFileName = "JPEG_" + auth?.currentUser?.uid + "_.png"
            val storageRef = storage?.reference?.child("post")?.child(imageFileName)
            storageRef?.putFile(photouri!!)?.addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener {


                    //이미지 주소
                    content.imageUrl = it.toString()
                    //유저의 UID
                    content.uid = auth?.currentUser?.uid
                    //게시물의 설명
                    content.explain = rootView?.textExplain?.text.toString()
                    //컨텐츠 주소
                    content.address = addresses
                    //컨텐츠 위도,경도 정보
                    content.latitude =uLatitude
                    content.longtiude=uLongitude
                    //유저의 아이디
                    content.userId = auth?.currentUser?.email
                    //게시물 업로드 시간
                    content.timestamp = System.currentTimeMillis()

//                var date = Date(System.currentTimeMillis())
//                Log.d("timetest","$date") // Tue Jun 08 22:13:16 GMT+09:00 2021 형식 출력
//                var mformat = SimpleDateFormat("yyyy-mm-dd - HH:mm:ss") //date 형식 파싱
//                var pdatetime = mformat.format(date) // 파싱결과 변수 담기


                    //게시물을 데이터를 생성
                    firestore?.collection("post")?.document("${auth?.currentUser?.email}+${System.currentTimeMillis()}")?.set(content)
                    Toast.makeText(mContext, "글쓰기를 완료했습니다.", Toast.LENGTH_LONG)
                        .show()

                    // 액티비티 재실행. -> home 으로
                    (activity as MainActivity).initNavigationBar()

                }?.addOnFailureListener {
                    Toast.makeText(mContext, "글등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        } else {

            //이미지 주소
            content.imageUrl = null
            //유저의 UID
            content.uid = auth?.currentUser?.uid
            //게시물의 설명
            content.explain = rootView?.textExplain?.text.toString()
            //컨텐츠 주소
            content.address = addresses
            //컨텐츠 위도,경도 정보
            content.latitude =uLatitude
            content.longtiude=uLongitude
            //유저의 아이디
            content.userId = auth?.currentUser?.email
            //게시물 업로드 시간
            content.timestamp = System.currentTimeMillis()

//                var date = Date(System.currentTimeMillis())
//                Log.d("timetest","$date") // Tue Jun 08 22:13:16 GMT+09:00 2021 형식 출력
//                var mformat = SimpleDateFormat("yyyy-mm-dd - HH:mm:ss") //date 형식 파싱
//                var pdatetime = mformat.format(date) // 파싱결과 변수 담기


            //게시물을 데이터를 생성
            firestore?.collection("post")?.document("${auth?.currentUser?.email}+${System.currentTimeMillis()}")?.set(content)
            Toast.makeText(mContext, "글쓰기를 완료했습니다.", Toast.LENGTH_LONG)
                .show()

            // 액티비티 재실행. -> home 으로
            (activity as MainActivity).initNavigationBar()

        }

    }
    // 키워드 검색 함수
    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
               addItemsAndMarkers(response.body())
                Log.d("Test", "Raw: ${response.raw()}")
                Log.d("Test", "Body: ${response.body()}")
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("MainActivity", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private  fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            mapView!!.removeAllPOIItems() // 지도의 마커 모두 제거

            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                mapView!!.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

        }
        // else 일 경우 : 검색결과 없을 경우 알림 삭제. autoCompleteText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
    }

}







