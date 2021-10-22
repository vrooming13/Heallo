package com.example.heallo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.heallo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(){


    private var homeFragment = HomeFragment()
    private var gridFragment = GridFragment()
    private var postFragment = PostFragment()
    private var userInfoFragment = UserInfoFragment()

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




    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fregments_frame,fragment)
            transaction.commit()
        }
    }

}