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


class NewsfeedAdapter(private val ctx: Context, var news: ArrayList<PostModel>,var onClickListener: View.OnClickListener) :
    RecyclerView.Adapter<NewsfeedAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageIV: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val avatarIV: ImageView = view.findViewById<View>(R.id.avatarIV) as ImageView
        val podcastTitleTV: TextView = view.findViewById<View>(R.id.podcastTitleTV) as TextView
        val durationTV: TextView = view.findViewById<View>(R.id.durationTV) as TextView
        val postDescriptonTV: TextView = view.findViewById<View>(R.id.postDescriptonTV) as TextView
        val postLL: LinearLayout = view.findViewById<View>(R.id.postLL) as LinearLayout


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.newsfeed_item, parent, false)

        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post = getItem(position)
        Glide.with(ctx).load(post.imageUri).fitCenter().into(holder.imageIV)
        Glide.with(ctx).load(R.mipmap.avatar).fitCenter().into(holder.avatarIV)
        holder.podcastTitleTV.text = post.title
        holder.postDescriptonTV.text = post.postDescriptionTV
        holder.durationTV.text = "Длительность: ${post.duration}"
        holder.postLL.setOnClickListener(onClickListener)
    }


    override fun getItemCount(): Int {
        return news.size
    }

    private fun getItem(position: Int): PostModel {
        return news[position]
    }

}