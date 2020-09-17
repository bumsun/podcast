package com.mountainasdheads.cast.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.models.MusicModel
import com.mountainasdheads.cast.models.TimeCodeModel
import com.mountainasdheads.cast.ui.activity.MainActivity
import com.mountainasdheads.cast.ui.adapters.TimeCodesAdapter
import com.mountainasdheads.cast.utils.PodcastHeap
import com.mountainasdheads.cast.utils.changeImageControlState
import com.mountainasdheads.cast.utils.interfaces.OnDeleteTimecode
import com.mountainasdheads.cast.utils.interfaces.OnMusicPicked
import com.mountainasdheads.cast.utils.waveform.WaveformFragment
import kotlinx.android.synthetic.main.bottom_sheet_music.view.*
import kotlinx.android.synthetic.main.fragment_edit_podcast.view.*
import kotlinx.android.synthetic.main.toolbar.view.*


open class EditPodcastFragment : WaveformFragment() {
    lateinit var timeCodesRV : RecyclerView
    lateinit var addTimeCodeLL : LinearLayout
    lateinit var mainLL : LinearLayout
    lateinit var playIV : ImageView
    lateinit var cutIV : ImageView
    lateinit var undoIV : ImageView
    lateinit var musicIV : ImageView
    lateinit var growIV : ImageView
    lateinit var downIV : ImageView
    lateinit var saveBTN : Button
    var isPlaying = false
    var isCut = false
    var isUndo = false
    var isMusic = false
    var isGrow = false
    var isDown = false
    var podcast = PodcastHeap
    var bgMusicModel : MusicModel? = null
    var isInitialized = false
    companion object {
        val TAG: String = EditPodcastFragment::class.java.simpleName
        fun newInstance() = EditPodcastFragment()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val myView = inflater.inflate(R.layout.fragment_edit_podcast, container, false)
//        setUpView(myView)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        isInitialized = true
        setUpView(myView)
    }

    override fun onPause2() {
        Log.d("myLogs", "onPause3();")
        try{
            changeImageControlState(playIV, !mIsPlaying, R.drawable.ic_play_blue, R.drawable.ic_pause_gray, context!!)
        }catch (err:java.lang.Exception){

        }

    }
    private fun setUpView(view : View){
        setUpToolbar("Редактирование", view.titleTV, view.leftIV, this)
        view.leftIV.setOnClickListener {
            (activity as MainActivity).back(PreviewPodcastFragment.newInstance())
        }
        timeCodesRV = view.timeCodesRV
        addTimeCodeLL = view.addTimeCodeLL
        mainLL = view.mainLL
        playIV = view.playIV
        cutIV = view.cutIV
        undoIV = view.undoIV
        musicIV = view.musicIV
        growIV = view.growIV
        downIV = view.downIV
        saveBTN = view.saveBTN
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL;
        timeCodesRV.layoutManager = linearLayoutManager
        setListeners()
    }
    private fun setListeners(){
        addTimeCodeLL.setOnClickListener {
            PodcastHeap.timecodes.add(TimeCodeModel(time = getSeconds()))
            val adapter = TimeCodesAdapter(context!!, PodcastHeap.timecodes, object : OnDeleteTimecode{
                override fun onDeleted() {
                    if (PodcastHeap.timecodes.size == 0){
                        timeCodesRV.visibility = View.GONE
                    }
                }

            })
            timeCodesRV.adapter = adapter
            timeCodesRV.visibility = View.VISIBLE
        }
        playIV.setOnClickListener {

            changeImageControlState(playIV, mIsPlaying, R.drawable.ic_play_blue, R.drawable.ic_pause_gray, context!!)
            onPlay(mStartPos)
        }
        cutIV.setOnClickListener {
            resetCut()
        }
        undoIV.setOnClickListener {
            resetEffects()
            if (isMusic) {
                removeMusic()
            }
        }
        musicIV.setOnClickListener {
            if (!isMusic) {
                startMusicPickerFragment()
            } else {

                showDialogNotificationAction()
            }
        }
        growIV.setOnClickListener {
            isGrow = changeImageControlState(growIV, isGrow, R.drawable.ic_volume_grow_gray, R.drawable.ic_volume_grow_blue, context!!)
            turnFadeIn()
        }
        downIV.setOnClickListener {
            isDown = changeImageControlState(downIV, isDown, R.drawable.ic_volume_down_gray, R.drawable.ic_volume_down_blue, context!!)
            turnFadeOut()
        }

        saveBTN.setOnClickListener {(activity as MainActivity).back(PreviewPodcastFragment.newInstance())}
        mainLL.setOnClickListener {}
    }
    override fun onTuchedWaveForm(){
        if (isInitialized) {
            val imageId = if (!mIsPlaying){
                R.drawable.ic_play_blue
            } else {
                R.drawable.ic_pause_gray
            }
            playIV.setImageDrawable(ContextCompat.getDrawable(context!!, imageId))


        }
    }

    private fun getSeconds() : String{
        val seekMsec =
            mWaveformView.pixelsToMillisecs((mTouchStart + mOffset).toInt()) / 1000
        val minutes = (seekMsec % 3600) / 60;
        val seconds = seekMsec % 60;

        val timeString = String.format("%02d:%02d", minutes.toInt(), seconds.toInt())
        return timeString
    }
    private fun startMusicPickerFragment(){
        (activity as MainActivity).setMusicPickerFragment(object : OnMusicPicked {
            override fun musicPicked(musicModel: MusicModel) {
                if (!isMusic) {
                    changeMusicState()
                }
                bgMusicModel = musicModel
                turnBackgroundMusic(bgMusicModel!!.id)
            }

        })
    }
    fun changeMusicState(){
        isMusic = changeImageControlState(
            musicIV,
            isMusic,
            R.drawable.ic_music_gray,
            R.drawable.ic_music_blue,
            context!!
        )
    }
    private fun removeMusic(){
        bgMusicModel = null
        turnBackgroundMusic(0)
        changeMusicState()
    }
    private var mBottomDialogNotificationAction: BottomSheetDialog? = null

    private fun showDialogNotificationAction() {
        try {
            val sheetView: View = activity!!.layoutInflater
                .inflate(R.layout.bottom_sheet_music, null)
            mBottomDialogNotificationAction = BottomSheetDialog(activity!!)
            mBottomDialogNotificationAction!!.setContentView(sheetView)
            mBottomDialogNotificationAction!!.show()
            val changeMusicLL = sheetView.changeMusicLL
            val deleteMusicLL = sheetView.deleteMusicLL
            val bottomSheet =
                mBottomDialogNotificationAction!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet!!.background = null

            changeMusicLL.setOnClickListener {
                startMusicPickerFragment()
                mBottomDialogNotificationAction?.hide()
            }
            deleteMusicLL.setOnClickListener {
                removeMusic()
                mBottomDialogNotificationAction?.hide()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}