package com.savana.core.extension

import android.content.Context

fun Int.pxToDp(context: Context): Float {
    return this / context.resources.displayMetrics.density
}

fun Float.pxToDp(context: Context): Float {
    return this / context.resources.displayMetrics.density
}