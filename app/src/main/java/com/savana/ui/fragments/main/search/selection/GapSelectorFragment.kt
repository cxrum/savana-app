package com.savana.ui.fragments.main.search.selection

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.savana.R
import com.savana.core.extension.getBitMap
import com.savana.databinding.FragmentGapSelectorBinding
import com.savana.domain.models.SelectedTrackGap
import com.savana.ui.activities.main.MainViewModel
import com.savana.ui.player.AudioPlayerViewModel
import com.savana.ui.player.AudioPlayerViewModel.Companion.MIN_DURATION_SECONDS
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@UnstableApi
class GapSelectorFragment : Fragment() {

    private val gapSelectionViewModel: GapSelectionViewModel by viewModel()
    private val audioPlayerViewModel: AudioPlayerViewModel by viewModel()

    private val mainViewModel: MainViewModel by activityViewModel()

    private var _binding: FragmentGapSelectorBinding? = null
    private val binding get() = _binding!!

    private val args: GapSelectorFragmentArgs by navArgs()
    private lateinit var navController: NavController

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            gapSelectionViewModel.loadAudioFile(it, requireActivity().contentResolver, requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGapSelectorBinding.inflate(inflater, container, false)
        mainViewModel.setCaption("Gap selection")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val audioUri = args.audioUri
        val fileName = args.fileName

        if (audioUri != null && fileName != null){
            gapSelectionViewModel.state.value.fileUri = audioUri
            gapSelectionViewModel.loadAudioFile(audioUri, requireActivity().contentResolver, requireContext())
            gapSelectionViewModel.setFilename(fileName)
        }

        setupUIFromState()
        setupUIListeners()
        observeViewModel()
    }

    private fun setupUIFromState(){
        val cover = gapSelectionViewModel.state.value.cover
        val currentSec = audioPlayerViewModel.currentPositionSec.value
        val gapStartSec = gapSelectionViewModel.state.value.startSec
        val audioUri = gapSelectionViewModel.state.value.fileUri

        audioPlayerViewModel.seekToSec(currentSec)
        gapSelectionViewModel.setCover(cover)

        if (gapStartSec!=null){
            gapSelectionViewModel.onRangeSelected(gapStartSec, gapStartSec+MIN_DURATION_SECONDS)
        }
    }

    private fun setupUIListeners() {

        binding.selectAnotherFileButton.setOnClickListener {
            filePickerLauncher.launch("audio/*")
        }

        binding.playPauseButton.setOnClickListener {
            audioPlayerViewModel.changePlayingState(!audioPlayerViewModel.isPlaying.value)
        }

        binding.confirmButtonAction.setOnClickListener {
            val range = binding.songGapSelector.getCurrentRange()
            mainViewModel.startMusicAnalyzingProcess(
                SelectedTrackGap(
                    gapStart = range.first * 1000L,
                    gapEnd = range.second * 1000L,
                    trackData = gapSelectionViewModel.currentAudioBytes.value,
                    trackTitle = gapSelectionViewModel.state.value.title!!
                )
            )
        }

        binding.songGapSelector.onRangeChanged { startSec, endSec ->
            gapSelectionViewModel.onRangeSelected(startSec, endSec)
            audioPlayerViewModel.seekToSec(startSec)
        }

        binding.cancelButton.setOnClickListener {
            onBack()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    gapSelectionViewModel.currentAudioBytes.collect { bytes ->
                        if (bytes.isNotEmpty()) {
                            audioPlayerViewModel.prepareAudio(bytes)
                        }
                    }
                }

                launch {
                    audioPlayerViewModel.isPlaying.collect { isPlaying ->
                        binding.playPauseButton.setImageResource(
                            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                        )
                    }
                }

                launch {
                    audioPlayerViewModel.isAudioPrepared.collect { isPrepared ->
                        if (isPrepared) {
                            audioPlayerViewModel.seekToSec(0)
                        }
                    }
                }

                launch {
                    audioPlayerViewModel.totalDurationSec
                        .filter { durationSec -> durationSec > 0
                                && audioPlayerViewModel.isAudioPrepared.value
                        }
                        .collectLatest { durationSec ->
                            setTotalDuration(durationSec)

                            if (durationSec < MIN_DURATION_SECONDS) {
                                setGapDuration(durationSec)
                                selectionLock()
                                gapSelectionViewModel.showError(
                                    Exception(requireContext().getString(R.string.the_song_too_short_exeption))
                                )
                            } else {
                                setGapDuration(MIN_DURATION_SECONDS)
                                selectionUnlock()
                                gapSelectionViewModel.clearError()
                            }
                        }
                }

                launch {
                    audioPlayerViewModel.currentPositionSec.collect { currentSec ->
                        setCurrentSecond(currentSec)
                    }
                }

                launch {
                    gapSelectionViewModel.state.collect { state ->
                        binding.songNameTextView.text =
                            state.title ?: state.fileName ?: getString(R.string.unknown_track)

                        setCover(state.cover)

                        state.errorMessage?.let { message ->
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                            gapSelectionViewModel.clearError()
                        }

                        if(state.isLoading){
                            startLoadingAnimation()
                        }else{
                            stopLoadingAnimation()
                        }
                    }
                }

                launch {
                    audioPlayerViewModel.mediaMetadata
                        .collect { metadata ->
                        val extractedTitle = metadata?.title?.toString()
                        val extractedArtworkData = metadata?.artworkData

                        gapSelectionViewModel.setTitle(extractedTitle)
                        gapSelectionViewModel.setCover(extractedArtworkData?.getBitMap())
                    }
                }
            }
        }
    }

    private fun startLoadingAnimation(){
        binding.songGapSelector.startLoadingAnimation()
    }

    private fun stopLoadingAnimation(){
        binding.songGapSelector.stopLoadingAnimation()
    }

    private fun setTotalDuration(seconds: Int){
        binding.songGapSelector.setTotalDuration(seconds)
    }

    private fun setCurrentSecond(currentSec: Int){
        if (currentSec >= 0) {
            binding.songGapSelector.setCurrentPlayedSeconds(currentSec)
        }
    }

    private fun setGapDuration(trackDurationSec: Int){
        if (trackDurationSec > MIN_DURATION_SECONDS){
            binding.songGapSelector.setGapDuration(MIN_DURATION_SECONDS)
        }

        if (trackDurationSec < MIN_DURATION_SECONDS){
            binding.songGapSelector.setGapDuration(trackDurationSec)
        }
    }

    private fun setCover(cover: Bitmap?){
        if (cover != null){
            binding.albumArtImageView.setImageBitmap(cover)
        }else{
            binding.albumArtImageView.setImageResource(R.drawable.bg_music_album_placehoder)
        }
    }

    private fun selectionUnlock(){
        binding.confirmButtonAction.isEnabled = true
    }

    private fun selectionLock(){
        binding.confirmButtonAction.isEnabled = false
    }

    private fun onBack(){
        val action = GapSelectorFragmentDirections
            .actionGapSelectorFragmentToSearchMainFragment()
        navController.navigate(action)
    }

    override fun onPause() {
        super.onPause()
        if (audioPlayerViewModel.isPlaying.value){
            audioPlayerViewModel.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (audioPlayerViewModel.isPlaying.value){
            audioPlayerViewModel.pause()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        gapSelectionViewModel.clearError()
    }

}