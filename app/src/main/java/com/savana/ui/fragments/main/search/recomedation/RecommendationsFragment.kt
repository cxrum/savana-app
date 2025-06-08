package com.savana.ui.fragments.main.search.recomedation

import ScreenSlidePagerAdapter
import android.os.Bundle
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
import com.savana.ui.fragments.main.search.recomedation.RecommendationViewModel.ScreenState
import kotlinx.coroutines.launch

@UnstableApi
class RecommendationsFragment : Fragment() {

    private val recommendationViewModel: RecommendationViewModel  by viewModel()
    private val mainViewModel: MainViewModel  by activityViewModel()

    private var _binding: FragmentRecomedationsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecomedationsBinding.inflate(inflater, container, false)
        mainViewModel.setCaption("Savana Recommendation")

        setListeners()
        setObservers()

        return binding.root
    }

    private fun setObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                recommendationViewModel.state.collect{ state ->

                    if (state.trackAuthor != null){
                        binding.author.text = state.trackAuthor
                    }

                    if (state.trackTitle != null){
                        binding.trackTitle.text = state.trackTitle
                    }

                }

            }
        }
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