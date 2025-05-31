package com.savana.ui.fragments.main.search.recomedation.componets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.savana.databinding.ViewSongPlayerBinding
import com.savana.domain.models.RecommendedTrack
import com.savana.ui.view.songplayer.SongPlayerView

class RecommendedTrackDiffCallback : DiffUtil.ItemCallback<RecommendedTrack>() {
    override fun areItemsTheSame(oldItem: RecommendedTrack, newItem: RecommendedTrack): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecommendedTrack, newItem: RecommendedTrack): Boolean {
        return oldItem == newItem
    }
}

class RecommendationListAdapter(
    private val onPlayPauseClicked: (track: RecommendedTrack, position: Int, wantsToPlay: Boolean) -> Unit,
    private val onSeekOccurred: (track: RecommendedTrack, position: Int, newProgressSeconds: Int) -> Unit,
    private val onItemClicked: (track: RecommendedTrack, position: Int) -> Unit
) : ListAdapter<RecommendedTrack, RecommendationListAdapter.TrackHolder>(RecommendedTrackDiffCallback()) {

    class TrackHolder(
        internal val playerView: SongPlayerView
    ) : RecyclerView.ViewHolder(playerView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val binding = ViewSongPlayerBinding.inflate(LayoutInflater.from(parent.context), parent)
        val songPlayer = binding.root as SongPlayerView

        return TrackHolder(songPlayer)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val track = getItem(position)

        holder.playerView.apply {
            setSongName(track.trackTitle)
            setTotalDurationSeconds(track.totalDurationSeconds)

            setCurrentProgressSeconds(track.currentProgressSeconds)
            setPlayingState(track.isPlaying)

            onPlayPauseToggleListener = { wantsToPlay ->
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onPlayPauseClicked(getItem(currentPosition), currentPosition, wantsToPlay)
                }
            }

            onSeekBarChangedByUserListener = { newProgressSeconds ->
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onSeekOccurred(getItem(currentPosition), currentPosition, newProgressSeconds)
                }
            }

            setOnClickListener {
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(currentPosition), currentPosition)
                }
            }
        }
    }
}