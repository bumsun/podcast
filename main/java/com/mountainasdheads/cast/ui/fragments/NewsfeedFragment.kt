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
import com.mountainasdheads.cast.ui.adapters.NewsfeedAdapter
import com.mountainasdheads.cast.utils.PodcastHeap
import com.psssum.market.utils.L
import kotlinx.android.synthetic.main.fragment_create_new.view.*
import kotlinx.android.synthetic.main.fragment_newsfeed.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

class NewsfeedFragment : BaseNavigationFragment() {
    lateinit var newsfeedRV : RecyclerView
    companion object {
        val TAG: String = NewsfeedFragment::class.java.simpleName
        fun newInstance() = NewsfeedFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_newsfeed, container, false)
        setUpView(view)
        return view
    }
    private fun setUpView(view : View){
        setUpToolbar("Новости", view.titleTV, view.leftIV, this)
        view.leftIV.visibility = View.GONE
        newsfeedRV = view.newsfeedRV
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL;
        newsfeedRV.layoutManager = linearLayoutManager

        val adapter = NewsfeedAdapter(context!!, PodcastHeap.posts, View.OnClickListener {
            (activity as MainActivity).setMyPodcastsFragment()
        })
        newsfeedRV.adapter = adapter
    }

}