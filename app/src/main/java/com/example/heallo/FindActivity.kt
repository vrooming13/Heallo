package com.example.heallo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.heallo.databinding.ActivityFindBinding

class FindActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindBinding
    private var findFragment = FindFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFindBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addFragment(findFragment)


    }
    private fun addFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.container,fragment) //위치지정
            transaction.commit()
        }
    }

}