package com.mountainasdheads.cast.ui.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mountainasdheads.cast.R
import com.mountainasdheads.cast.ui.activity.MainActivity
import com.mountainasdheads.cast.utils.PathUtils
import com.mountainasdheads.cast.utils.PodcastHeap
import kotlinx.android.synthetic.main.fragment_podcast_details.view.*
import kotlinx.android.synthetic.main.toolbar.view.*


class PodcastDetailsFragment: BaseNavigationFragment() {
    var isBadContent = false
    var isExport = false
    var isTrailer = true
    private val PICK_IMAGE = 100
    private val PICK_AUDIO = 1001
    private var imageUri: Uri? = null
    private var audioUri: Uri? = null
    lateinit var photoIV: ImageView
    lateinit var nextBTN: Button
    lateinit var pickAudioBTN: Button
    lateinit var pickImageRL: RelativeLayout
    lateinit var mainLL: LinearLayout
    lateinit var cardView: CardView
    lateinit var trailerLL: LinearLayout
    lateinit var exportLL: LinearLayout
    lateinit var badContentLL: LinearLayout
    lateinit var trailerIV: ImageView
    lateinit var exportIV: ImageView
    lateinit var badContentIV: ImageView
    lateinit var podcastTitleET: EditText
    lateinit var descriptionTV: EditText
    lateinit var audioPickerLL: LinearLayout
    lateinit var pickedAudioLL: LinearLayout
    lateinit var pickAnotherAudioLL: LinearLayout
    lateinit var editAudioBTN: Button
    lateinit var podcastNameTV: TextView
    lateinit var durationTV: TextView
    var duration = "00:03"
    companion object {
        val TAG: String = PodcastDetailsFragment::class.java.simpleName
        fun newInstance() = PodcastDetailsFragment()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_podcast_details, container, false)
        setUpView(view)
        return view
    }
    private fun setUpView(view : View){
        setUpToolbar("Новый подкаст", view.titleTV, view.leftIV, this)
        photoIV = view.photoIV
        nextBTN = view.nextBTN
        pickImageRL = view.pickImageRL
        pickAudioBTN = view.pickAudioBTN
        cardView = view.cardView
        trailerLL = view.trailerLL
        exportLL = view.exportLL
        badContentLL = view.badContentLL
        trailerIV = view.trailerIV
        exportIV = view.exportIV
        badContentIV = view.badContentIV
        mainLL = view.mainLL
        podcastTitleET = view.podcastTitleET
        descriptionTV = view.descriptionTV
        audioPickerLL = view.audioPickerLL
        pickedAudioLL = view.pickedAudioLL
        pickAnotherAudioLL = view.pickAnotherAudioLL
        editAudioBTN = view.editAudioBTN
        podcastNameTV = view.podcastNameTV
        durationTV = view.durationTV
        setListeners()
    }
    private fun setPodcastHeap(){
        PodcastHeap.title = podcastTitleET.text.toString()
        PodcastHeap.description = descriptionTV.text.toString()
        PodcastHeap.imageUri = imageUri
        PodcastHeap.audioUri = audioUri
        PodcastHeap.duration = duration
    }
    private fun setListeners(){
        nextBTN.setOnClickListener {
            (activity as MainActivity).setPreviewFragment()
            setPodcastHeap()
        }
        pickAudioBTN.setOnClickListener {openMusic()}
        pickImageRL.setOnClickListener {openGallery()}
        trailerLL.setOnClickListener {
            isTrailer = !isTrailer
            changeState(isTrailer, trailerIV)
        }
        exportLL.setOnClickListener {
            isExport = !isExport
            changeState(isExport, exportIV)
        }
        badContentLL.setOnClickListener {
            isBadContent = !isBadContent
            changeState(isBadContent, badContentIV)
        }
        mainLL.setOnClickListener {
            PodcastHeap.title = podcastTitleET.text.toString()
            PodcastHeap.description = descriptionTV.text.toString()
            if (imageUri != null){
                PodcastHeap.imageUri = imageUri
            }
            if (audioUri != null){
                PodcastHeap.audioUri = audioUri
            }

        }
        descriptionTV.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                checkIsReadyToCreate()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        podcastTitleET.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                checkIsReadyToCreate()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        pickAnotherAudioLL.setOnClickListener { openMusic() }
        editAudioBTN.setOnClickListener {
            setPodcastHeap()
            (activity as MainActivity).setEditFragment() }


    }
    private fun changeState(isShow : Boolean, image : ImageView){
        var imageId = if (isShow){
            R.drawable.ic_state_on
        } else {
            R.drawable.ic_state_off
        }
        image.setImageDrawable( context?.let { it1 -> ContextCompat.getDrawable(it1, imageId) })
    }
    private fun checkIsReadyToCreate(){
        if (audioUri != null && imageUri != null && descriptionTV.text != null && descriptionTV.text.toString() != "" && podcastTitleET.text != null && podcastTitleET.text.toString() != ""){
            nextBTN.alpha = 1.0f
            nextBTN.isEnabled = true
        } else {
            nextBTN.alpha = 0.4f
            nextBTN.isEnabled = false
        }
    }
    private fun openGallery() {
        val gallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }
    private fun checkRunTimePermission() {
        val permissionArrays = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111)
        } else {
            startMusicIntent()
        }
    }
    private fun openMusic() {
        checkRunTimePermission()

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&  grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            startMusicIntent()
        }
    }
    private fun startMusicIntent(){
        val intent_upload = Intent()
        intent_upload.type = "audio/*"
        intent_upload.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent_upload, PICK_AUDIO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            Glide.with(context!!).load(imageUri).fitCenter().into(photoIV)
            cardView.visibility = View.VISIBLE
        } else if (resultCode == RESULT_OK && requestCode == PICK_AUDIO){
            audioUri = data?.data
            if (audioUri != null) {
                audioPickerLL.visibility = View.GONE
                pickedAudioLL.visibility = View.VISIBLE

                podcastNameTV.text = queryName(context!!.contentResolver, audioUri!!)
                //менять длительность
                duration = PathUtils.getSecondsCount(PathUtils.getPath(context!!, audioUri!!))

                durationTV.text = duration
            }
        }
        checkIsReadyToCreate()
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        try {
            val returnCursor: Cursor = resolver.query(uri, null, null, null, null)!!
            val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name: String = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        } catch (e : Exception){
            return "music.mp3"
        }
    }

}