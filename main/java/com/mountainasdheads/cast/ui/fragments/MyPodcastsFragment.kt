package com.mountainasdheads.cast.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.ui.activity.MainActivity
import com.mountainasdheads.cast.ui.adapters.MyPodcastsAdapter
import com.mountainasdheads.cast.ui.adapters.NewsfeedAdapter
import com.mountainasdheads.cast.utils.PodcastHeap
import kotlinx.android.synthetic.main.fragment_create_new.view.*
import kotlinx.android.synthetic.main.fragment_my_podcasts.view.*
import kotlinx.android.synthetic.main.toolbar.view.*

class MyPodcastsFragment : BaseNavigationFragment() {
    lateinit var podcastsRV : RecyclerView
    lateinit var addBTN : Button
    companion object {
        val TAG: String = MyPodcastsFragment::class.java.simpleName
        fun newInstance() = MyPodcastsFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_podcasts, container, false)
        setUpView(view)
        return view
    }
    private fun setUpView(view : View){
        setUpToolbar("Мои подкасты", view.titleTV, view.leftIV, this)
        view.leftIV.visibility = View.GONE
        podcastsRV = view.podcastsRV
        addBTN = view.addBTN
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL;
        podcastsRV.layoutManager = linearLayoutManager

        val adapter = MyPodcastsAdapter(context!!, PodcastHeap.posts, View.OnClickListener {

        })
        podcastsRV.adapter = adapter
        addBTN.setOnClickListener {
            (activity as MainActivity).setDetailsFragment()
        }
    }

}