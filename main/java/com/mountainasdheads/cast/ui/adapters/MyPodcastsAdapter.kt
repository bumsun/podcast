package com.mountainasdheads.cast.ui.adapters

import android.R.id.button1
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.models.PostModel
import com.mountainasdheads.cast.models.TimeCodeModel
import com.mountainasdheads.cast.utils.PodcastHeap
import com.mountainasdheads.cast.utils.interfaces.OnDeleteTimecode

import com.psssum.market.utils.L
import kotlinx.android.synthetic.main.timecode_item.view.*


class MyPodcastsAdapter(private val ctx: Context, var news: ArrayList<PostModel>,var onClickListener: View.OnClickListener) :
    RecyclerView.Adapter<MyPodcastsAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoIV: ImageView = view.findViewById<View>(R.id.photoIV) as ImageView
        val podcastTitleTV: TextView = view.findViewById<View>(R.id.podcastTitleTV) as TextView
        val durationTV: TextView = view.findViewById<View>(R.id.durationTV) as TextView
        val podcastLL: LinearLayout = view.findViewById<View>(R.id.podcastLL) as LinearLayout


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_podcasts_item, parent, false)

        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post = getItem(position)
        Glide.with(ctx).load(post.imageUri).fitCenter().into(holder.photoIV)
        holder.podcastTitleTV.text = post.title
        holder.durationTV.text = "Длительность: ${post.duration}"
        holder.podcastLL.setOnClickListener(onClickListener)
    }


    override fun getItemCount(): Int {
        return news.size
    }

    private fun getItem(position: Int): PostModel {
        return news[position]
    }

}