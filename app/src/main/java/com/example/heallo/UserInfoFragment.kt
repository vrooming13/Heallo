package com.example.heallo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class UserInfoFragment : Fragment() {

  /*  companion object {
        const val TAG : String = "로그"

        fun newInstance() : UserInfoFragment {
            return UserInfoFragment()
        }
    }
//프레그먼트가 메모리에 올라갔을때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
//프레그먼트를 안고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }*/
    //뷰가 생성되었을때
    //프레그먼트와 레이아웃을 연결시켜부는 부분
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fregment_user_info, container, false)
        //프레그먼트 내정보랑 연결
        return view
    }
}