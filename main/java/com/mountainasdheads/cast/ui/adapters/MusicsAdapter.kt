package com.mountainasdheads.cast.ui.adapters

import android.R.id.button1
import android.content.Context
import android.media.Image
import android.media.MediaMetadataRetriever
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.models.MusicModel
import com.mountainasdheads.cast.models.TimeCodeModel
import com.mountainasdheads.cast.utils.PodcastHeap
import com.mountainasdheads.cast.utils.interfaces.OnDeleteTimecode
import com.mountainasdheads.cast.utils.interfaces.OnMusicPicked

import com.psssum.market.utils.L
import kotlinx.android.synthetic.main.timecode_item.view.*


class MusicsAdapter(private val ctx: Context, var music: ArrayList<MusicModel>, var onMusicPicked: OnMusicPicked) :
    RecyclerView.Adapter<MusicsAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageIV: ImageView = view.findViewById<View>(R.id.imageIV) as ImageView
        val titleTV: TextView = view.findViewById<View>(R.id.titleTV) as TextView
        val artistTV: TextView = view.findViewById<View>(R.id.artistTV) as TextView
        val durationTV: TextView = view.findViewById<View>(R.id.durationTV) as TextView
        val musicItemLL: LinearLayout =
            view.findViewById<View>(R.id.musicItemLL) as LinearLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.music_item, parent, false)

        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val music = getItem(position)
        holder.titleTV.text = music.title
        holder.artistTV.text = music.artist
        holder.durationTV.text = music.duration
        Glide.with(ctx).load(music.album).fitCenter().into(holder.imageIV)
        holder.musicItemLL.setOnClickListener {
            onMusicPicked.musicPicked(music)
        }
    }

    public fun updateMusicList(musicList: ArrayList<MusicModel>){
        music = musicList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return music.size
    }

    private fun getItem(position: Int): MusicModel {
        return music[position]
    }

}