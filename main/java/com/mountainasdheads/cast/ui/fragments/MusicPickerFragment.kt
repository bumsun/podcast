package com.mountainasdheads.cast.ui.fragments

import android.annotation.SuppressLint
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.models.MusicModel
import com.mountainasdheads.cast.ui.activity.MainActivity
import com.mountainasdheads.cast.ui.adapters.MusicsAdapter
import com.mountainasdheads.cast.utils.interfaces.OnMusicPicked
import kotlinx.android.synthetic.main.fragment_music_picker.view.*
import kotlinx.android.synthetic.main.toolbar.view.*


class MusicPickerFragment : BaseNavigationFragment() {
    lateinit var musicRV : RecyclerView
    lateinit var mainLL : RelativeLayout
    lateinit var searchET : EditText
    lateinit var notFoundTV : TextView
    lateinit var onMusicPicked: OnMusicPicked
    lateinit var adapter: MusicsAdapter
    val musicList = ArrayList<MusicModel>()
    companion object {
        val TAG: String = MusicPickerFragment::class.java.simpleName
        fun newInstance() = MusicPickerFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_music_picker, container, false)
        setUpView(view)
        return view
    }
    private fun setUpView(view : View){
        setUpToolbar("Выбрать музыку", view.titleTV, view.leftIV, this)
        addRightToolbarImage(view.rightIV, View.OnClickListener {

        })
        musicRV = view.musicRV
        searchET = view.searchET
        notFoundTV = view.notFoundTV
        mainLL = view.mainLL
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL;
        musicRV.layoutManager = linearLayoutManager

        musicList.add(MusicModel("Lineage","The hunter village", "02:17", R.raw.lineage, "https://sun9-43.userapi.com/jEQB8fv9j19SIpbHKOsUANclKp6AyTs566i0AA/syjMBeiKHIQ.jpg"))
        musicList.add(MusicModel("Мелодия любви","Frederic Chopin", "02:43", R.raw.frederik_chopin, "https://sun9-46.userapi.com/Vm-bGQsxis2C7ICLfmAtTB7KGCzvx6-Ip8kOoQ/3V_QLRDzPw4.jpg"))

        adapter = MusicsAdapter(context!!, musicList, object : OnMusicPicked{
            override fun musicPicked(musicModel: MusicModel) {
                onMusicPicked.musicPicked(musicModel)
                (activity as MainActivity).back(PreviewPodcastFragment.newInstance())
            }
        })
        musicRV.adapter = adapter

        searchET.addTextChangedListener(object : TextWatcher{
            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(s: Editable?) {
                val filteredMusic: List<MusicModel> = musicList.filter { musicModel -> musicModel.artist.toLowerCase().contains(s.toString().toLowerCase()) || musicModel.title.toLowerCase().contains(s.toString().toLowerCase()) }
                adapter.updateMusicList(ArrayList(filteredMusic))
                if (filteredMusic.isNotEmpty()){
                    notFoundTV.visibility = View.GONE
                } else {
                    notFoundTV.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mainLL.setOnClickListener {  }
    }

}