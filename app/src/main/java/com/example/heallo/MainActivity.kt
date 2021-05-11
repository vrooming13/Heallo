package com.example.heallo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_recyclerview.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { // 초기 앱 실행시 view 생성
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview) // xml 디자인 보여줌

        val profileList = arrayListOf(
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

        rv_profile.adapter = ProfileAdapter(profileList)
    }

}