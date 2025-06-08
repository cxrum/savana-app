import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.savana.ui.fragments.main.search.recomedation.RecommendationViewModel.ScreenState
import com.savana.ui.fragments.main.search.recomedation.componets.AnalyticsContentFragment
import com.savana.ui.fragments.main.search.recomedation.componets.RecommendationContentFragment

class ScreenSlidePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            ScreenState.Recommendations.ordinal -> RecommendationContentFragment()
            ScreenState.Analytics.ordinal -> AnalyticsContentFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}