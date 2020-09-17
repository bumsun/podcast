package com.mountainasdheads.cast.utils.interfaces

import com.mountainasdheads.cast.models.MusicModel

interface OnMusicPicked {
    fun musicPicked(musicModel: MusicModel)
}