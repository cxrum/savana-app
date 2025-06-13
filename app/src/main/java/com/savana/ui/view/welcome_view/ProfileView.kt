package com.savana.ui.view.welcome_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.savana.R
import com.savana.databinding.ViewProfileBinding

class ProfileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var _binding: ViewProfileBinding? = null
    private val binding get() = _binding!!


    init {
        _binding = ViewProfileBinding.inflate(LayoutInflater.from(context), this, true)

        loadAttributes(attrs)
    }

    private fun loadAttributes(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.SongGapSelectorView, 0, 0) {

            }
        }
    }

    fun setOnLogoutClicked(callback: ()->Unit){
        binding.logout.setOnClickListener {
            callback()
        }
    }
}