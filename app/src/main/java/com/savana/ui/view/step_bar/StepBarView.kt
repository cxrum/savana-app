package com.savana.ui.view.step_bar

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.savana.R

class StepBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var step: Int = 0

    var maxSteps: Int = 0
    var minSteps: Int = 0


    init {
        loadAttributes(attrs)
    }

    private fun loadAttributes(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.StepBarView, 0, 0) {

            }
        }
    }
}