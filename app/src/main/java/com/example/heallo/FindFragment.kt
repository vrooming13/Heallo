package com.example.heallo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import com.example.heallo.databinding.FragmentFindBinding



class FindFragment : Fragment() {

       private var findidFragment = FindIdFragment()
       private var findPasswordFragment = FindPasswordFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = FragmentFindBinding.inflate(LayoutInflater.from(container?.context),container,false)

        // 컨테이너 (배경)클릭 시 키보드 숨김/
        view.container.setOnClickListener {
//            Log.d("button","click to container button")
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                HIDE_NOT_ALWAYS
            )
        }
        view.idFind.setOnClickListener{
//            Log.d("button","click id find button")
            addtobackstackFragment(findidFragment)

        }

        view.passwordFind.setOnClickListener {
//            Log.d("button","click password find button")
            addtobackstackFragment(findPasswordFragment)

        }

        return view.root
    }
    // add + backstack 을 이용하면 뒤로가기시 이전 프레그먼트로 이동 가능.
    private fun addtobackstackFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container,fragment) //위치지정
            .addToBackStack(null)
            transaction.commit()
        }
    }

}