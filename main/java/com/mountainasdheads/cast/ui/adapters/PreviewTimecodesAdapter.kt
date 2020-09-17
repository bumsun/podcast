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
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.models.TimeCodeModel
import com.mountainasdheads.cast.utils.PodcastHeap
import com.mountainasdheads.cast.utils.interfaces.OnDeleteTimecode

import com.psssum.market.utils.L
import kotlinx.android.synthetic.main.timecode_item.view.*


class PreviewTimecodesAdapter(private val ctx: Context, var timecodes: ArrayList<TimeCodeModel>) :
    RecyclerView.Adapter<PreviewTimecodesAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timecodeTimeTV: TextView = view.findViewById<View>(R.id.timecodeTimeTV) as TextView
        val timecodeNameTV: TextView = view.findViewById<View>(R.id.timecodeNameTV) as TextView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.preview_timecodes_item, parent, false)

        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val timecode = getItem(position)
        holder.timecodeNameTV.setText(" — ${timecode.name}")
        holder.timecodeTimeTV.setText(timecode.time)
    }


    override fun getItemCount(): Int {
        return timecodes.size
    }

    private fun getItem(position: Int): TimeCodeModel {
        return timecodes[position]
    }

}