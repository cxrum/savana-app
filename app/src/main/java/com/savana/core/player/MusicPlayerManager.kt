package com.savana.core.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.savana.core.exeption.SongException
import com.savana.data.local.song.ByteArrayDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.media3.common.MediaMetadata

class MusicPlayerManager(
    context: Context
) {

    private var exoPlayer: ExoPlayer? = ExoPlayer.Builder(context).build()
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs

    private val _durationMs = MutableStateFlow(0L)
    val durationMs: StateFlow<Long> = _durationMs

    private val _playerError = MutableLiveData<SongException?>()
    val playerError: LiveData<SongException?> = _playerError

    private val _mediaMetadata = MutableStateFlow<MediaMetadata?>(null)
    val mediaMetadata: StateFlow<MediaMetadata?> = _mediaMetadata

    init {
        exoPlayer?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                _isPlaying.value = isPlayingValue
                if (isPlayingValue) {
                    startProgressUpdates()
                    _playerError.postValue(null)
                } else {
                    stopProgressUpdates()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    _durationMs.value = exoPlayer?.duration ?: 0L
                } else if (playbackState == Player.STATE_ENDED) {
                    exoPlayer?.seekTo(0)
                    exoPlayer?.playWhenReady = false
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                _playerError.postValue(SongException("Error: ${error.message})"))
                _isPlaying.value = false
                stopProgressUpdates()
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onMediaMetadataChanged(mediaMetadata)
                _mediaMetadata.value = mediaMetadata
            }

        })
    }

    @UnstableApi
    fun prepare(byteArray: ByteArray) {
        try {
            val dataSourceFactory = ByteArrayDataSource.Factory(byteArray)
            val mediaItem = MediaItem.Builder()
                .setUri("data://custom/audio.mp3")
                .setMimeType("audio/mpeg")
                .build()

            val mediaSource = androidx.media3.exoplayer.source.ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            exoPlayer?.setMediaSource(mediaSource)
            exoPlayer?.prepare()
            _playerError.postValue(null)
        } catch (e: Exception) {
            _playerError.postValue(SongException("Unable to load audio: ${e.message}"))
        }
    }

    fun changePlayingState(state: Boolean) {
        if (state) {
            exoPlayer?.play()
        } else {
            if (exoPlayer?.playbackState == Player.STATE_IDLE || exoPlayer?.playbackState == Player.STATE_ENDED) {
                if (exoPlayer?.playbackState == Player.STATE_ENDED) exoPlayer?.seekTo(0)
                exoPlayer?.playWhenReady = true
            } else {
                exoPlayer?.pause()
            }
        }
    }


    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
        _currentPositionMs.value = positionMs
    }

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressJob = scope.launch {
            while (isActive && exoPlayer?.isPlaying == true) {
                _currentPositionMs.value = exoPlayer?.currentPosition ?: 0L
                delay(100L)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
        _currentPositionMs.value = exoPlayer?.currentPosition ?: 0L
    }

    fun clearError(){
        _playerError.value = null
    }

    fun releasePlayer() {
        stopProgressUpdates()
        exoPlayer?.release()
        exoPlayer = null
    }
}