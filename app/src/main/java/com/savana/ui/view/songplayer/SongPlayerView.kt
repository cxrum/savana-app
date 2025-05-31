package com.savana.ui.view.songplayer

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.savana.R
import com.savana.databinding.ViewSongPlayerBinding

class SongPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var _binding: ViewSongPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var songNameTextView: TextView
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalDurationTextView: TextView
    private lateinit var songSeekBar: SeekBar
    private lateinit var playPauseButton: ImageButton

    private var currentProgressSeconds: Int = 0
    private var totalDurationSeconds: Int = 0
    private var isActuallyPlaying: Boolean = false
    private var songTitle: String = ""

    @DrawableRes private var playIconRes: Int = R.drawable.ic_play
    @DrawableRes private var pauseIconRes: Int = R.drawable.ic_pause
    private var defaultSongNameText: String = "Track Name"

    var onPlayPauseToggleListener: ((wantsToPlay: Boolean) -> Unit)? = null

    var onSeekBarChangedByUserListener: ((progressSeconds: Int) -> Unit)? = null

    init {
        _binding = ViewSongPlayerBinding.inflate(LayoutInflater.from(context), this)
        bindViews()
        loadAttributes(attrs, defStyleAttr)
        applyAttributesToViews()
        setupListeners()
        updateUI()
    }



    private fun bindViews() {
        songNameTextView = binding.songName
        currentTimeTextView = binding.currentTime
        totalDurationTextView = binding.totalDurationTime
        songSeekBar = binding.seekBar
        playPauseButton = binding.toglePlay
    }

    private fun loadAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.SongPlayerView, defStyleAttr, 0) {
                songTitle = getString(R.styleable.SongPlayerView_trackTitle) ?: defaultSongNameText
                totalDurationSeconds = getInteger(R.styleable.SongPlayerView_totalDuration, 0)
            }
        }
    }

    private fun applyAttributesToViews() {
        setSongName(songTitle)
        setTotalDurationSeconds(totalDurationSeconds)
    }

    private fun setupListeners() {
        playPauseButton.setOnClickListener {
            val desiredPlayState = !isActuallyPlaying
            onPlayPauseToggleListener?.invoke(desiredPlayState)
        }

        songSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentTimeTextView.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    onSeekBarChangedByUserListener?.invoke(it.progress)
                }
            }
        })
    }

    fun setSongName(name: String) {
        songTitle = name
        songNameTextView.text = songTitle
    }

    fun setTotalDurationSeconds(durationSeconds: Int) {
        this.totalDurationSeconds = durationSeconds.coerceAtLeast(0)
        songSeekBar.max = this.totalDurationSeconds
        totalDurationTextView.text = formatTime(this.totalDurationSeconds)
        if (currentProgressSeconds > this.totalDurationSeconds) {
            currentProgressSeconds = this.totalDurationSeconds
        }
        updateProgressDisplay()
    }

    fun setCurrentProgressSeconds(seconds: Int) {
        currentProgressSeconds = seconds.coerceIn(0, totalDurationSeconds)
        songSeekBar.progress = currentProgressSeconds
        currentTimeTextView.text = formatTime(currentProgressSeconds)
    }

    fun setPlayingState(isPlaying: Boolean) {
        this.isActuallyPlaying = isPlaying
        updatePlayPauseButtonState()
    }


    private fun updateUI() {
        songNameTextView.text = songTitle
        totalDurationTextView.text = formatTime(totalDurationSeconds)
        currentTimeTextView.text = formatTime(currentProgressSeconds)
        songSeekBar.max = totalDurationSeconds
        songSeekBar.progress = currentProgressSeconds
        updatePlayPauseButtonState()
    }

    private fun updatePlayPauseButtonState() {
        playPauseButton.setImageResource(if (isActuallyPlaying) pauseIconRes else playIconRes)
    }

    private fun updateProgressDisplay() {
        songSeekBar.progress = currentProgressSeconds
        currentTimeTextView.text = formatTime(currentProgressSeconds)
    }

    private fun formatTime(totalSeconds: Int): String {
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%01d:%02d", minutes, seconds)
    }

}