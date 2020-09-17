package com.mountainasdheads.cast.utils

import android.net.Uri
import com.mountainasdheads.cast.models.PostModel
import com.mountainasdheads.cast.models.TimeCodeModel

object PodcastHeap {
    var title = ""
    var description = ""
    var duration = ""
    var imageUri : Uri? = null
    var audioUri : Uri? = null
    var timecodes = ArrayList<TimeCodeModel>()
    var posts = ArrayList<PostModel>()
}