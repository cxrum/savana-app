package com.savana.ui.view.songplayer

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.savana.R

class SongGapSelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    init {
        loadAttributes(attrs)
    }

    private fun loadAttributes(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.SongGapSelectorView, 0, 0) {

            }
        }
    }
}