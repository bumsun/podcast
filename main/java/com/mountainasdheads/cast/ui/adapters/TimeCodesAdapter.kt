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


class TimeCodesAdapter(private val ctx: Context, var timecodes: ArrayList<TimeCodeModel>,var onDeleteTimecode: OnDeleteTimecode) :
    RecyclerView.Adapter<TimeCodesAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timecodeNameET: EditText = view.findViewById<View>(R.id.timecodeNameET) as EditText
        val timecodeTimeET: EditText = view.findViewById<View>(R.id.timecodeTimeET) as EditText
        val deleteTimecodeRL: RelativeLayout =
            view.findViewById<View>(R.id.deleteTimecodeRL) as RelativeLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.timecode_item, parent, false)

        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val timecode = getItem(position)
        holder.deleteTimecodeRL.setOnClickListener {
            timecodes.removeAt(position)
            notifyDataSetChanged()
            onDeleteTimecode.onDeleted()
        }
        holder.timecodeNameET.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                timecodes[position].name = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        holder.timecodeTimeET.setText(timecode.time)
        holder.timecodeNameET.setText(timecode.name)
        holder.timecodeTimeET.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                timecodes[position].time = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

    }


    override fun getItemCount(): Int {
        return timecodes.size
    }

    private fun getItem(position: Int): TimeCodeModel {
        return timecodes[position]
    }

}