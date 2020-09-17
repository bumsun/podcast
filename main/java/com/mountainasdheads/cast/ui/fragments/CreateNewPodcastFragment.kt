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
import com.psssum.market.utils.L
import kotlinx.android.synthetic.main.fragment_create_new.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

class CreateNewPodcastFragment : BaseFragment() {
    lateinit var createBTN : Button
    companion object {
        val TAG: String = CreateNewPodcastFragment::class.java.simpleName
        fun newInstance() = CreateNewPodcastFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_new, container, false)
        setUpView(view)
        return view
    }
    private fun setUpView(view : View){
        createBTN = view.createBTN
        createBTN.setOnClickListener {
            setDetailsFragment()
        }
    }

}