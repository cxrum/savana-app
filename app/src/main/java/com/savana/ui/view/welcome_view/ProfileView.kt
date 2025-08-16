package com.savana.ui.view.welcome_view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import coil.load
import coil.transform.CircleCropTransformation
import com.savana.R
import com.savana.databinding.ViewProfileBinding
import com.savana.domain.models.user.UserData

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

    fun setUserInfo(userData: UserData){
        binding.username.text = userData.nickname
        binding.userPfp.load(userData.avatar) {
            crossfade(true)
            placeholder(R.drawable.ic_user_placeholder)
            error(R.drawable.ic_user_placeholder)
            transformations(CircleCropTransformation())
        }
    }

    fun showUsernameShimmer() {
        binding.usernameShimmer.startShimmer()
        binding.usernameShimmer.visibility = VISIBLE
        binding.username.visibility = GONE
    }

    fun hideUsernameShimmer() {
        binding.usernameShimmer.stopShimmer()
        binding.usernameShimmer.visibility = GONE
        binding.username.visibility = VISIBLE
    }
}