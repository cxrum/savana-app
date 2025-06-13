package com.savana.ui.fragments.main.search.recomedation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import android.content.Context
import com.savana.domain.models.TrackInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class MusicPlayerViewModel(
    private val applicationContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var positionUpdateJob: Job? = null

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(applicationContext).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _uiState.update { it.copy(isPlaying = isPlaying) }
                    if (isPlaying) {
                        startPositionUpdates()
                        if (playbackState == Player.STATE_ENDED) {
                            playNextTrack()
                        }
                    } else {
                        stopPositionUpdates()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    mediaItem?.mediaId?.let { trackId ->
                        val newTrack = _uiState.value.trackInfos.find { it.id == trackId.toInt() }
                        _uiState.update {
                            it.copy(
                                currentPlayingTrackInfo = newTrack,
                                totalDurationMillis = (newTrack?.totalDurationSeconds!! * 1000L),
                                currentPositionMillis = 0L
                            )
                        }
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            _uiState.update { it.copy(isPreparingTrack = true) }
                        }
                        Player.STATE_READY -> {
                            _uiState.update { it.copy(isPreparingTrack = false, totalDurationMillis = exoPlayer?.duration ?: 0L) }
                        }
                        Player.STATE_ENDED -> {
                            _uiState.update { it.copy(isPreparingTrack = false) }
                            playNextTrack()
                        }
                        Player.STATE_IDLE -> {
                            _uiState.update { it.copy(isPreparingTrack = false) }
                        }
                    }
                }
            })
        }
    }

    fun loadTracks(trackInfos: List<TrackInfo>) {
        _uiState.update { it.copy(trackInfos = trackInfos) }
    }

    fun onTrackSelected(trackInfo: TrackInfo) {
        val currentPlayer = exoPlayer ?: return

        if (_uiState.value.currentPlayingTrackInfo?.id == trackInfo.id && _uiState.value.isPlaying) {
            currentPlayer.pause()
        } else if (_uiState.value.currentPlayingTrackInfo?.id == trackInfo.id && !_uiState.value.isPlaying) {
            currentPlayer.play()
        } else {
            _uiState.update { it.copy(currentPlayingTrackInfo = trackInfo) }
            prepareAndPlayTrack(trackInfo)
        }
    }

    private fun prepareAndPlayTrack(trackInfo: TrackInfo) {
        val currentPlayer = exoPlayer ?: return
        val mediaItem = MediaItem.Builder()
            .setUri(trackInfo.streamUrl)
            .setMediaId(trackInfo.id.toString())
            .build()

        currentPlayer.setMediaItem(mediaItem)
        currentPlayer.prepare()
        currentPlayer.play()
        _uiState.update { it.copy(currentPlayingTrackInfo = trackInfo, totalDurationMillis = trackInfo.totalDurationSeconds * 1000L, currentPositionMillis = 0L) }
    }


    fun playPauseToggle() {
        val currentPlayer = exoPlayer ?: return
        if (currentPlayer.isPlaying) {
            currentPlayer.pause()
        } else {
            if (currentPlayer.currentMediaItem == null && _uiState.value.trackInfos.isNotEmpty()) {
                _uiState.value.trackInfos.firstOrNull()?.let { onTrackSelected(it) }
            } else {
                currentPlayer.play()
            }
        }
    }

    fun playNextTrack() {
        val currentPlayer = exoPlayer ?: return
        val tracks = _uiState.value.trackInfos
        val currentTrack = _uiState.value.currentPlayingTrackInfo
        if (tracks.isEmpty()) return

        val currentIndex = currentTrack?.let { tracks.indexOf(it) } ?: -1
        val nextIndex = if (currentIndex != -1 && currentIndex < tracks.size - 1) currentIndex + 1 else 0

        tracks.getOrNull(nextIndex)?.let {
            onTrackSelected(it)
        }
    }

    fun playPreviousTrack() {
        val currentPlayer = exoPlayer ?: return
        val tracks = _uiState.value.trackInfos
        val currentTrack = _uiState.value.currentPlayingTrackInfo
        if (tracks.isEmpty()) return

        val currentIndex = currentTrack?.let { tracks.indexOf(it) } ?: -1
        val previousIndex = if (currentIndex > 0) currentIndex - 1 else tracks.size - 1

        tracks.getOrNull(previousIndex)?.let {
            onTrackSelected(it)
        }
    }

    fun seekTo(positionFraction: Float) {
        val currentPlayer = exoPlayer ?: return
        val duration = currentPlayer.duration
        if (duration > 0) {
            val newPosition = (duration * positionFraction).toLong()
            currentPlayer.seekTo(newPosition)
            _uiState.update { it.copy(currentPositionMillis = newPosition) }
        }
    }

    fun seekToMillis(positionMillis: Long) {
        exoPlayer?.let { player ->
            val duration = player.duration
            if (duration > 0) {
                val newPosition = positionMillis.coerceIn(0, duration)
                player.seekTo(newPosition)
                _uiState.update { it.copy(currentPositionMillis = newPosition) }
            }
        }
    }


    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = viewModelScope.launch {
            while (_uiState.value.isPlaying) {
                _uiState.update {
                    it.copy(currentPositionMillis = exoPlayer?.currentPosition ?: it.currentPositionMillis)
                }
                delay(1000)
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPositionUpdates()
        exoPlayer?.release()
        exoPlayer = null
    }
}