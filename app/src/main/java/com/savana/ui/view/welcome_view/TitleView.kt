package com.savana.ui.view.welcome_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.savana.R
import com.savana.databinding.ViewTitleBinding

class TitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewTitleBinding? = null
    private val binding get() = _binding!!

    init {
        orientation = VERTICAL
        _binding = ViewTitleBinding.inflate(LayoutInflater.from(context), this, true)
        loadAttributes(attrs)
    }

    fun setSubtitleText(text: CharSequence?) {
        binding.tvSubtitle.text = text
    }

    fun setTitleText(text: CharSequence?) {
        binding.tvTitle.text = text
    }

    private fun loadAttributes(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.TitleView, 0, 0) {
                setTitleText(getString(R.styleable.TitleView_title) ?: "None")
                setSubtitleText(getString(R.styleable.TitleView_subtitle) ?: "None")
            }
        }
    }
}