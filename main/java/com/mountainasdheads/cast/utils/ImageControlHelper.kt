package com.mountainasdheads.cast.utils

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.mountainasdheads.cast.R

fun changeImageControlState(imageView: ImageView, isToogled : Boolean, standartImageId : Int, toogledImageId : Int, context : Context) : Boolean{
    val mIsToogled = !isToogled
    val imageId = if (!mIsToogled){
        standartImageId
    } else {
        toogledImageId
    }
    imageView.setImageDrawable(ContextCompat.getDrawable(context, imageId))
    return mIsToogled
}