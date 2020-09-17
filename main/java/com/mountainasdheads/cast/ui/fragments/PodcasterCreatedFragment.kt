package com.mountainasdheads.cast.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.ui.activity.MainActivity
import com.psssum.market.utils.L
import kotlinx.android.synthetic.main.fragment_create_new.view.*
import kotlinx.android.synthetic.main.fragment_podcast_created.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

class PodcasterCreatedFragment : BaseFragment() {
    lateinit var shareBTN : Button
    companion object {
        val TAG: String = PodcasterCreatedFragment::class.java.simpleName
        fun newInstance() = PodcasterCreatedFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_podcast_created, container, false)
        setUpView(view)
        return view
    }
    private fun setUpView(view : View){
        shareBTN = view.shareBTN
        shareBTN.setOnClickListener {
            (activity as MainActivity).setNewsfeedEditFragment()
        }
    }

}