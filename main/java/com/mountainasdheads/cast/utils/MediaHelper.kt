package com.mountainasdheads.cast.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.io.File


object MediaHelper {
    fun getAudioName(uri: Uri, context : Context){
        val path: String = File(uri.path).getCanonicalPath()
        val c: Cursor = context.getContentResolver().query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.YEAR
            ),
            MediaStore.Audio.Media.DATA + " = ?", arrayOf(
                path
            ),
            ""
        )!!

        if (null == c) {
            // ERROR
        }

        while (c.moveToNext()) {
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.TRACK))
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE))
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA))
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION))
            c.getString(c.getColumnIndex(MediaStore.Audio.Media.YEAR))
        }
    }
}