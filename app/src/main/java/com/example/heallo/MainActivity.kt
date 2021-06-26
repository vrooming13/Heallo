package com.example.heallo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_recyclerview.*

class MainActivity : AppCompatActivity(){


    private var homeFragment = HomeFragment()
    private var gridFragment = GridFragment()
    private var postFragment = PostFragment()
    private var userInfoFragment = UserInfoFragment()

    override fun onCreate(savedInstanceState: Bundle?) { // 초기 앱 실행시 view 생성
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // xml 디자인 보여줌
        replaceFragment(homeFragment)
        Toast.makeText(
            this@MainActivity,
            "로그인 성공.",
            Toast.LENGTH_SHORT
        ).show()
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_favorite -> replaceFragment(gridFragment)
                R.id.ic_post -> replaceFragment(postFragment)
                R.id.ic_userInfo -> replaceFragment(userInfoFragment)
            }
            true
        }


        //리사이클러 뷰에 출력되는 데이터 베열
        /*val profileList = arrayListOf(
                profiles(R.drawable.man, "김홍민", 25 , "깔끔합니다." ),
                profiles(R.drawable.girl, "김소연", 24 , "별로에요." ),
                profiles(R.drawable.man, "남정혁", 24 , "운동 하기 딱 좋아요." ),
                profiles(R.drawable.man, "오광식", 25 , "시설 좋아요" ),
                profiles(R.drawable.girl, "김유리", 20 , "별로에요." ),
                profiles(R.drawable.man, "남짱구", 21 , "좋아요." ),
                profiles(R.drawable.man, "오철수", 23 , "쓰레기 많아요" ),
                profiles(R.drawable.girl, "김진구", 24 , "힐링 하고 가요" ),
                profiles(R.drawable.man, "퉁퉁이", 24 , "득근 득근~" ),
                profiles(R.drawable.man, "비실이", 25 , "실외 시설 강력 추천" ),
                profiles(R.drawable.girl, "이슬이", 15 , "굿" )
        )

        rv_profile.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_profile.setHasFixedSize(true)

        rv_profile.adapter = ProfileAdapter(profileList)*/

    }




    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fregments_frame,fragment)
            transaction.commit()
        }
    }

}