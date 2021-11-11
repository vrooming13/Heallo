package com.example.heallo

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.heallo.databinding.ActivityMainBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity(){

    //  by lazy 를 통해 프레그먼트를 사용할 때 초기화하여 성능 개선.
    private val homeFragment by lazy { HomeFragment() }
    private val gridFragment by lazy { GridFragment() }
    private val postFragment by lazy { PostFragment() }
    private val userInfoFragment by lazy { UserInfoFragment() }
    private var lastTimeBackPressed : Long = 0

    private var view : ActivityMainBinding? =null

    override fun onCreate(savedInstanceState: Bundle?) { // 초기 앱 실행시 view 생성
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view!!.root) // xml 디자인 보여줌
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  // 화면켜짐 유지
        initNavigationBar()
//        getHashKey()

    }


    fun initNavigationBar() {

        replaceFragment(homeFragment)
        view?.bottomNavigationView?.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_favorite -> replaceFragment(gridFragment)
                R.id.ic_post -> replaceFragment(postFragment)
                R.id.ic_userInfo -> replaceFragment(userInfoFragment)
            }
            true
        }
    }

//       //hash key 조회
//    fun getHashKey(){
//        var packageInfo : PackageInfo = PackageInfo()
//        try {
//            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
//        } catch (e: PackageManager.NameNotFoundException){
//            e.printStackTrace()
//        }
//
//        for (signature: Signature in packageInfo.signatures){
//            try{
//                var md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.e("KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT))
//            } catch(e: NoSuchAlgorithmException){
//                Log.e("KEY_HASH", "Unable to get MessageDigest. signature = " + signature, e)
//            }
//        }
//    }



    // 1.5초내 뒤로가기 두번 = 종료
    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500){
            if(supportFragmentManager.backStackEntryCount == 0 ){
                lastTimeBackPressed = System.currentTimeMillis()
                Toast.makeText(this,"'뒤로' 버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_LONG).show()
            }else{
                //뒤로가기.
                super.onBackPressed()
            }
        }
        else {
            ActivityCompat.finishAffinity(this)
            System.runFinalization()
            System.exit(0)
        }
    }


    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fregments_frame,fragment)
            transaction.commit()
        }
    }

}