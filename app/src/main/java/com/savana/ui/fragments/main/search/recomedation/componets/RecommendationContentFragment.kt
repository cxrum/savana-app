package com.savana.ui.fragments.main.search.recomedation.componets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.savana.R
import com.savana.databinding.FragmentRecommendationsContentBinding
import com.savana.ui.fragments.main.search.recomedation.MusicPlayerViewModel
import com.savana.ui.fragments.main.search.recomedation.RecommendationViewModel
import com.savana.ui.theme.SavanaTheme
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


@Composable
fun MusicPlayerScreenForScrollView(
    viewModel: MusicPlayerViewModel,
    recommendationViewModel: RecommendationViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val tracksData by recommendationViewModel.recommendationData.collectAsState()

    LaunchedEffect(tracksData.trackInfos) {
        if (tracksData.trackInfos.isNotEmpty()) {
            viewModel.loadTracks(tracksData.trackInfos)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TrackListView(
            trackInfos = uiState.trackInfos,
            currentPlayingTrackInfo = uiState.currentPlayingTrackInfo,
            isPlaying = uiState.isPlaying,
            onTrackClick = { track -> viewModel.onTrackSelected(track) },
            modifier = Modifier
                .height(400.dp)
        )
    }
}

class RecommendationContentFragment : Fragment(R.layout.fragment_recommendations_content) {

    private val musicPlayerViewModel: MusicPlayerViewModel  by viewModel()
    private val recommendationViewModel: RecommendationViewModel by activityViewModel()

    private var _binding: FragmentRecommendationsContentBinding? = null
    private val binding: FragmentRecommendationsContentBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationsContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSongPlayList()
    }

    private fun setupSongPlayList() {
        val composeView = binding.musicPlayerComposeView
        composeView.setContent {
            SavanaTheme {
                MusicPlayerScreenForScrollView(
                    musicPlayerViewModel,
                    recommendationViewModel
                )
            }
        }
    }
}