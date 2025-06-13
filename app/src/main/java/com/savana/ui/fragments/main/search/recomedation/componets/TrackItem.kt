package com.savana.ui.fragments.main.search.recomedation.componets
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.savana.R
import com.savana.core.utils.formatTime
import com.savana.domain.models.TrackInfo

@Composable
fun TrackItemView(
    trackInfo: TrackInfo,
    isPlaying: Boolean,
    isSelected: Boolean,
    onTrackClick: (TrackInfo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onTrackClick(trackInfo) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            AsyncImage(
//                model = track.albumArtUrl,
//                contentDescription = "Album Art for ${track.title}",
//                modifier = Modifier
//                    .size(56.dp)
//                    .clip(MaterialTheme.shapes.medium),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trackInfo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = trackInfo.artistName,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = formatTime(trackInfo.totalDurationSeconds),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp)
            )
            IconButton(onClick = { onTrackClick(trackInfo) }) {
                Icon(
                    imageVector = if (isPlaying && isSelected) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying && isSelected) "Pause" else "Play ${trackInfo.title}",
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TrackListView(
    trackInfos: List<TrackInfo>,
    currentPlayingTrackInfo: TrackInfo?,
    isPlaying: Boolean,
    onTrackClick: (TrackInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    if (trackInfos.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.track_list_empty))
        }
        return
    }
    LazyColumn(modifier = modifier) {
        items(trackInfos, key = { it.id }) { track ->
            TrackItemView(
                trackInfo = track,
                isPlaying = isPlaying,
                isSelected = track.id == currentPlayingTrackInfo?.id,
                onTrackClick = onTrackClick,
            )
        }
    }
}