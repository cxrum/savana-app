package com.savana.core.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun ByteArray.getBitMap(): Bitmap?{
    return this.let {
        if (it.isNotEmpty()) {
            try {
                BitmapFactory.decodeByteArray(it, 0, it.size)
            } catch (e: Exception) {
                android.util.Log.e("MusicPlayerManager", "Error decoding artwork: ${e.message}")
                null
            }
        } else {
            android.util.Log.e("MusicPlayerManager", "Cover isn`t exist")
            null
        }
    }
}