package com.example.heallo

import net.daum.mf.map.api.MapView
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.heallo.databinding.FragmentPostBinding
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import net.daum.mf.map.api.MapPoint


import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton



class PostFragment : Fragment() {

    lateinit var mContext: Context
    private var homeFragment = HomeFragment()

   private var rootView : FragmentPostBinding?= null
    //kakao map api permission
    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    val PERM_STORAGE = 102

    /////firebase
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var auth: FirebaseAuth? = null
    private var uLatitude : Double?= null
    private var uLongitude : Double?=null
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
        val mapView = net.daum.mf.map.api.MapView(activity)

        val permissionCheck = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                val userNowLocation: Location =
                    lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!! // 유저 현재위치 저장.
                 uLatitude = userNowLocation.latitude  // 사용자 위도
                 uLongitude = userNowLocation.longitude // 사용자 경도
                val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude!!, uLongitude!!) // 유저 현재 위치 저장.
                mapView.setMapCenterPoint(uNowPosition, true) //  kakao map 위도,경도를 통한 현재 위치 지정.
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




    private val REQUEST_ACCESS_FINE_LOCATION = 1000

//    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) =
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    requireActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                cancel()
//            } else {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    REQUEST_ACCESS_FINE_LOCATION
//                )
//            }
//        } else {
//            ok()
//        }
//
//    private fun showPermissionInfoDialog() {
//        alert("위치 정보를 얻으려면 위치 권한이 필요합니다", "권한이 필요한 이유") {
//            yesButton {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    REQUEST_ACCESS_FINE_LOCATION
//                )
//            }
//            noButton { }
//        }.show()
//    }


//    override fun onResume() {
//        super.onResume()
//        Log.d("ONRESUME", "RESUME")
//        // 권한 요청
//        permissionCheck(
//            cancel = { showPermissionInfoDialog() },   // 권한 필요 안내창
//            ok = { addLocationListener() }      //    주기적으로 현재 위치를 요청
//        )
//    }



    private fun contentUpload() {
        var content = ContentDTO()
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

                    //컨텐츠 위도,경도 정보
                    content.latitude =uLatitude!!
                    content.longtiude=uLongitude!!
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

            //컨텐츠 위도,경도 정보
            content.latitude =uLatitude!!
            content.longtiude=uLongitude!!
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




    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
    }

}





/*Toast.makeText(mContext, "글이 등록되었습니다", Toast.LENGTH_SHORT).show()*/

    //디비에 바인딩 할 위치 생성 및 컬렉션(테이블)에 데이터 집합 생성
    //시간 생성


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








    /*@RequiresApi(Build.VERSION_CODES.O)
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

                 if(uris != null){
                    image_upload(uris!!, email.toString(), dateAndtime)
                     activity?.let {
                         val intent = Intent(context, MainActivity::class.java) // 메인화면 홈프레그먼트 화면으로 이동
                         startActivity(intent)
                         Toast.makeText(mContext, "글쓰기를 완료했습니다.", Toast.LENGTH_LONG)
                             .show()
                         activity?.finish()
                     }
                 }else {
                     activity?.let {
                         val intent = Intent(context, MainActivity::class.java) // 메인화면 홈프레그먼트 화면으로 이동
                         startActivity(intent)
                         Toast.makeText(mContext, "글쓰기를 완료했습니다.", Toast.LENGTH_LONG)
                             .show()
                         activity?.finish()
                     }
                 }
             }
             .addOnFailureListener { Log.d("create_fail","fail") }
    }


    private fun image_upload(uri: Uri, email:String, dateAndtime: LocalDateTime){
        val mStorageRef = FirebaseStorage.getInstance().reference
        mStorageRef.child("$email"+"$dateAndtime").putFile(uri) // images 파일 명
    }*/

