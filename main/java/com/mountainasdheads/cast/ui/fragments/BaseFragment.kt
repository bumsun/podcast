package com.mountainasdheads.cast.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.ui.activity.MainActivity

open class BaseFragment : Fragment() {
    companion object {
        val TAG: String = BaseFragment::class.java.simpleName
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }
//    protected fun setUpToolbar(title : String, isShowToolbar : Boolean){
//        (activity as MainActivity).setUpToolbar(title, isShowToolbar)
//    }
//    protected fun changeFragment(fragment: BaseFragment){
//        (activity as MainActivity).replaceFragment(fragment, R.anim.enter_from_right)
//    }
    protected fun setDetailsFragment(){
        (activity as MainActivity).setDetailsFragment()
    }
}