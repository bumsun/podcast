package com.mountainasdheads.cast.ui.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.ui.activity.MainActivity
import com.psssum.market.utils.L
import kotlinx.android.synthetic.main.fragment_create_new.view.*
import kotlinx.android.synthetic.main.toolbar.*

open class BaseNavigationFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected fun setUpToolbar(title : String, titleTV : TextView, leftIV : ImageView, fragment: BaseFragment){
        titleTV.text = title
        leftIV.setOnClickListener {
            (activity as MainActivity).back(fragment)
        }
    }
    protected fun addRightToolbarImage(rightIV : ImageView, onClickListener : View.OnClickListener){
        rightIV.visibility = View.VISIBLE
        rightIV.setOnClickListener(onClickListener)
    }

}