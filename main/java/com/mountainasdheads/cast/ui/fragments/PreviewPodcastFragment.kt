package com.mountainasdheads.cast.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.ui.activity.MainActivity
import com.mountainasdheads.cast.ui.adapters.PreviewTimecodesAdapter
import com.mountainasdheads.cast.utils.PodcastHeap
import kotlinx.android.synthetic.main.fragment_create_new.view.*
import kotlinx.android.synthetic.main.fragment_preview_podcast.view.*
import kotlinx.android.synthetic.main.toolbar.view.*

class PreviewPodcastFragment : BaseNavigationFragment() {
    lateinit var mainLL : LinearLayout
    lateinit var timecodesRV : RecyclerView
    lateinit var photoIV : ImageView
    lateinit var podcastTitleTV : TextView
    lateinit var durationTV : TextView
    lateinit var descriptionTV : TextView
    lateinit var publishBTN : Button
    companion object {
        val TAG: String = PreviewPodcastFragment::class.java.simpleName
        fun newInstance() = PreviewPodcastFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_preview_podcast, container, false)
        setUpView(view)
        return view
    }
    private fun setUpView(view : View){
        setUpToolbar("Новый подкаст", view.titleTV, view.leftIV, this)
        mainLL = view.mainLL
        timecodesRV = view.timecodesRV
        photoIV = view.photoIV
        podcastTitleTV = view.podcastTitleTV
        durationTV = view.durationTV
        descriptionTV = view.descriptionTV
        publishBTN = view.publishBTN
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL;
        timecodesRV.layoutManager = linearLayoutManager
        val adapter = PreviewTimecodesAdapter(context!!, PodcastHeap.timecodes)
        timecodesRV.adapter = adapter
        if (PodcastHeap.imageUri != null){
            Glide.with(context!!).load(PodcastHeap.imageUri).fitCenter().into(photoIV)
            //photoIV.setImageURI(PodcastHeap.imageUri)
        }
        podcastTitleTV.text = PodcastHeap.title
        descriptionTV.text = PodcastHeap.description
        durationTV.text = "Длительность: ${PodcastHeap.duration}"

        setListeners()
    }
    private fun setListeners(){
        mainLL.setOnClickListener {  }
        publishBTN.setOnClickListener { (activity as MainActivity).setPodcastCreatedFragment() }
    }

}