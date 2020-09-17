package com.mountainasdheads.cast.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.models.PostModel
import com.mountainasdheads.cast.ui.activity.MainActivity
import com.mountainasdheads.cast.utils.PodcastHeap
import kotlinx.android.synthetic.main.fragment_create_new.view.*
import kotlinx.android.synthetic.main.fragment_newsfeed_edit.view.*
import kotlinx.android.synthetic.main.toolbar.view.*

class NewsFeedEditFragment : BaseNavigationFragment() {
    lateinit var postDescriptonTV : EditText
    lateinit var imageIV : ImageView
    lateinit var podcastTitleTV : TextView
    lateinit var durationTV : TextView
    companion object {
        val TAG: String = NewsFeedEditFragment::class.java.simpleName
        fun newInstance() = NewsFeedEditFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_newsfeed_edit, container, false)
        setUpView(view)
        return view
    }
    private fun setUpView(view : View){
        setUpToolbar("Матвей Правосудов", view.titleTV, view.leftIV, this)
        view.rightShareIV.visibility = View.VISIBLE
        view.rightShareIV.setOnClickListener {
            PodcastHeap.posts.add(PostModel(PodcastHeap.description, PodcastHeap.title, PodcastHeap.duration, PodcastHeap.imageUri!!, PodcastHeap.audioUri!!, postDescriptonTV.text.toString()))
            (activity as MainActivity).setNewsfeedFragment()
        }
        postDescriptonTV = view.postDescriptonTV
        imageIV = view.imageIV
        podcastTitleTV = view.podcastTitleTV
        durationTV = view.durationTV
        durationTV.text = "Длительность: ${PodcastHeap.duration}"
        podcastTitleTV.text = PodcastHeap.title
        postDescriptonTV.setText(PodcastHeap.description)
        Glide.with(context!!).load(PodcastHeap.imageUri).fitCenter().into(imageIV)
    }

}