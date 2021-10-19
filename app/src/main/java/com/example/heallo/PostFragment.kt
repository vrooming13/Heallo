package com.example.heallo


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

import com.example.heallo.databinding.FragmentPostBinding


import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.util.*


class PostFragment : Fragment() {

    lateinit var mContext: Context

   private var rootView : FragmentPostBinding?= null

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


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = FragmentPostBinding.inflate(LayoutInflater.from(container?.context),container,false)


        rootView!!.gallery.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERM_STORAGE)
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, PERM_STORAGE)
            onActivityResult(PERM_STORAGE, RESULT_OK, intent)
        }

        rootView!!.writeBtn.setOnClickListener {
//            contentUpload(mLatLng!!)// 성공시 fragment 전환 필요.
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
                        Log.i("URI", "${uri}")
                        photouri = uri
                        Log.i("URI2", "${photouri}")
                    }
                }
            }
        }
        //val uploadType = childFragmentManager.findFragmentById(R.id.imageView);
        //uploadType?.onActivityResult(requestCode, resultCode, data)

        //// 검색 자동완성
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
        alert("위치 정보를 얻으려면 위치 권한이 필요합니다", "권한이 필요한 이유") {
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
//                    addLocationListener()
                } else {
                    toast("권한이 거부 됨")
                }
                return
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        Log.d("ONRESUME", "RESUME")
//        // 권한 요청
//        permissionCheck(
//            cancel = { showPermissionInfoDialog() },   // 권한 필요 안내창
//            ok = { addLocationListener() }      //    주기적으로 현재 위치를 요청
//        )
//    }



    fun contentUpload() {
        val imageFileName = "JPEG_" + auth?.currentUser?.uid + "_.png"
        val storageRef = storage?.reference?.child("post")?.child(imageFileName)
        storageRef?.putFile(photouri!!)?.addOnSuccessListener {

            storageRef.downloadUrl.addOnSuccessListener {
                val contentDTO = ContentDTO()

                //이미지 주소
                contentDTO.imageUrl = it.toString()
                //유저의 UID
                contentDTO.uid = auth?.currentUser?.uid
                //게시물의 설명
                contentDTO.explain = rootView?.textarea?.text.toString()

                //유저의 아이디
                contentDTO.userId = auth?.currentUser?.email
                //게시물 업로드 시간
                contentDTO.timestamp = System.currentTimeMillis()
//                var date = Date(System.currentTimeMillis())
//                Log.d("timetest","$date") // Tue Jun 08 22:13:16 GMT+09:00 2021 형식 출력
//                var mformat = SimpleDateFormat("yyyy-mm-dd - HH:mm:ss") //date 형식 파싱
//                var pdatetime = mformat.format(date) // 파싱결과 변수 담기


                //게시물을 데이터를 생성
                firestore?.collection("post")?.document()?.set(contentDTO)

                requireActivity().setResult(RESULT_OK)
                activity?.let {
                    val intent = Intent(context, MainActivity::class.java) // 메인화면 홈프레그먼트 화면으로 이동
                    startActivity(intent)
                    Toast.makeText(mContext, "글쓰기를 완료했습니다.", Toast.LENGTH_LONG)
                        .show()
                    activity?.finish()
                }
            }?.addOnFailureListener {
                    Toast.makeText(mContext, "글등록에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
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

