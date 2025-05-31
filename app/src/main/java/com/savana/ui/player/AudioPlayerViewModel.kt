package com.savana.ui.player // Або ваш пакет для плеєра

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.savana.R
import com.savana.core.exeption.SongToShortException
import com.savana.core.player.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@UnstableApi
class AudioPlayerViewModel(application: Application) : ViewModel() {

    private val musicPlayerManager = MusicPlayerManager(application.applicationContext)

    val isPlaying: StateFlow<Boolean> = musicPlayerManager.isPlaying

    val currentPositionSec: StateFlow<Int> = musicPlayerManager.currentPositionMs
        .map { (it / 1000).toInt() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)

    val totalDurationSec: StateFlow<Int> = musicPlayerManager.durationMs
        .map { (it / 1000).toInt() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)

    val mediaMetadata: StateFlow<MediaMetadata?> = musicPlayerManager.mediaMetadata
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), null)

    private val _playerErrorMessage = MutableStateFlow<String?>(null)
    val playerErrorMessage: StateFlow<String?> = _playerErrorMessage.asStateFlow()

    private val _isAudioPrepared = MutableStateFlow(false)
    val isAudioPrepared: StateFlow<Boolean> = _isAudioPrepared.asStateFlow()

    init {
        viewModelScope.launch {
            musicPlayerManager.durationMs.collect { durationMs ->
                if (durationMs > 0 && _playerErrorMessage.value == null) {
                    _isAudioPrepared.value = true
                }
            }
        }
    }

    fun prepareAudio(audioBytes: ByteArray) {
        viewModelScope.launch {
            _playerErrorMessage.value = null
            _isAudioPrepared.value = false
            try {
                musicPlayerManager.prepare(audioBytes)
            } catch (e: Exception) {
                _playerErrorMessage.value = e.message ?: "Не вдалося підготувати аудіо"
                _isAudioPrepared.value = false
            }
        }
    }

    fun play() {
        if (isAudioPrepared.value) {
            musicPlayerManager.changePlayingState(true)
        } else {
            _playerErrorMessage.value = "Аудіо не підготовлено для відтворення."
        }
    }

    fun pause() {
        musicPlayerManager.changePlayingState(false)
    }

    fun changePlayingState(shouldPlay: Boolean) {
        if (shouldPlay) {
            play()
        } else {
            pause()
        }
    }

    fun seekToSec(seconds: Int) {
        if (isAudioPrepared.value) {
            musicPlayerManager.seekTo(seconds * 1000L)
        }
    }

    fun clearPlayerError() {
        _playerErrorMessage.value = null
    }

    fun releasePlayer() {
        musicPlayerManager.releasePlayer()
        _isAudioPrepared.value = false
        _playerErrorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object{
        const val MIN_DURATION_SECONDS = 30
    }
}