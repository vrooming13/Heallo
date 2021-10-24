package com.example.heallo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.heallo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){


    private var homeFragment = HomeFragment()
    private var gridFragment = GridFragment()
    private var postFragment = PostFragment()
    private var userInfoFragment = UserInfoFragment()
    private var lastTimeBackPressed : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) { // 초기 앱 실행시 view 생성
        super.onCreate(savedInstanceState)
        val view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root) // xml 디자인 보여줌
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  // 화면켜짐 유지
        replaceFragment(homeFragment)
        Toast.makeText(
            this@MainActivity,
            "로그인 성공.",
            Toast.LENGTH_SHORT
        ).show()
        view.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_favorite -> replaceFragment(gridFragment)
                R.id.ic_post -> replaceFragment(postFragment)
                R.id.ic_userInfo -> replaceFragment(userInfoFragment)
            }
            true
        }

    }
    // 1.5초내 뒤로가기 두번 = 종료
    override fun onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed >= 1500){
            lastTimeBackPressed = System.currentTimeMillis()
            Toast.makeText(this,"'뒤로' 버튼을 한번 더 누르시면 종료됩니다.",Toast.LENGTH_LONG).show() }
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