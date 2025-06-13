package com.savana.ui.fragments.main.search.recomedation

import ScreenSlidePagerAdapter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.util.UnstableApi
import com.savana.databinding.FragmentRecomedationsBinding
import com.savana.ui.activities.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.savana.R
import com.savana.domain.models.RecommendationData
import com.savana.ui.activities.main.OperationState
import com.savana.ui.fragments.main.search.recomedation.RecommendationViewModel.ScreenState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@UnstableApi
class RecommendationsFragment : Fragment() {

    private val recommendationViewModel: RecommendationViewModel  by activityViewModel()
    private val mainViewModel: MainViewModel  by activityViewModel()

    private var _binding: FragmentRecomedationsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecomedationsBinding.inflate(inflater, container, false)
        mainViewModel.setCaption("Savana Recommendation")

        setObservers()
        setListeners()

        return binding.root
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.recommendationResult.collect { state ->

                        Log.d("State", state.toString())

                        if (state is OperationState.Success<RecommendationData>) {
                            val recommendationData = state.data
                            recommendationViewModel.processRecommendationData(recommendationData)
                            mainViewModel.operationHandled()
                        }
                    }
                }


                launch {
                    recommendationViewModel.state.collect{ state ->
                        if (state.trackTitle != null && state.trackAuthor != null){
                            setUpTrackData(
                                state.trackTitle,
                                state.trackAuthor
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setUpTrackData(title: String, author: String){
        binding.trackTitle.text = title
        binding.author.text = author
    }

    private fun setListeners(){

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonRecommendation -> binding.viewPager.setCurrentItem(
                        ScreenState.Recommendations.ordinal, true
                    )

                    R.id.buttonAnalytics -> binding.viewPager.setCurrentItem(
                        ScreenState.Analytics.ordinal, true
                    )
                }
            }
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    ScreenState.Recommendations.ordinal -> {
                        binding.toggleGroup.check(R.id.buttonRecommendation)
                        recommendationViewModel.recommendationsScreen()
                    }
                    ScreenState.Analytics.ordinal -> {
                        binding.toggleGroup.check(R.id.buttonAnalytics)
                        recommendationViewModel.analyticsScreen()
                    }
                }
            }
        })
    }
}