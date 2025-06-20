package com.savana.ui.activities.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.savana.R
import com.savana.databinding.ActivityMainBinding
import com.savana.domain.models.HistoryEntry
import com.savana.domain.models.Status
import com.savana.domain.models.user.UserData
import com.savana.ui.activities.authentication.AuthenticationActivity
import com.savana.ui.activities.main.history.HistoryAdapter
import com.savana.ui.decorators.SpacingItemDecoration
import com.savana.ui.fragments.main.search.error.ErrorFragmentDirections
import com.savana.ui.fragments.main.search.loading.LoadingFragmentDirections
import com.savana.ui.fragments.main.search.recomedation.RecommendationViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModel()
    private val recommendationViewModel: RecommendationViewModel by viewModel()

    private lateinit var historyAdapter: HistoryAdapter

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainContentContainer) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationDrawerContentWrapper) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            windowInsets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController

        mainViewModel.historyForceUpdate()
        mainViewModel.userDataUpdate(this)

        setupHistoryAdapter()
        setupObservers()
        setupListeners()
    }

    private fun setupObservers(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    mainViewModel.mainState.collect{ state ->
                        setCaption(state.caption ?: "")
                    }
                }

                launch {
                    mainViewModel.recommendationResult.collect{ result ->
                        when(result){
                            is OperationState.Error -> handleMusicAnalyzeError(result.errorMessage)
                            is OperationState.Idle -> handleAnalyzeIdle()
                            is OperationState.Loading -> handleAnalyzeLoading(
                                result.msg,
                                result.canLeaveScreen
                            )
                            is OperationState.Success<*> -> handleMusicAnalyzeSuccess()
                        }
                    }
                }

                launch {
                    mainViewModel.history.collect{ state ->
                        if (state.isLoading){
                            showLoadingHistory()
                        }else{
                            val history = state.history
                            if (history.isEmpty()){
                                showEmptyHistory()
                            }else{
                                historyAdapter.submitList(history)
                                showHistory()
                            }
                        }
                    }

                }
            }

        }
        mainViewModel.userData.observe(this){ data ->
            setupUserInfo(data)
        }
    }

    private fun showLoadingHistory(){
        binding.layerHistory.historyRecyclerview.visibility = View.GONE
        binding.layerHistory.emptyHistory.visibility = View.GONE
        binding.layerHistory.historyLoading.root.visibility = View.VISIBLE
    }

    private fun showHistory(){
        binding.layerHistory.historyRecyclerview.visibility = View.VISIBLE
        binding.layerHistory.emptyHistory.visibility = View.GONE
        binding.layerHistory.historyLoading.root.visibility = View.GONE
    }

    private fun showEmptyHistory(){
        binding.layerHistory.historyRecyclerview.visibility = View.GONE
        binding.layerHistory.emptyHistory.visibility = View.VISIBLE
        binding.layerHistory.historyLoading.root.visibility = View.GONE
    }

    private fun setupListeners(){
        with(binding){
            menuButton.setOnClickListener {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }

            drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerOpened(drawerView: View) {
                    menuButton.isActivated = true
                    mainViewModel.historyUpdate(this@MainActivity)
                }

                override fun onDrawerClosed(drawerView: View) {
                    menuButton.isActivated = false
                }
            })

            layerHistory.user.setOnLogoutClicked{
                mainViewModel.logout()
                goToAuthentication()
            }
        }
    }

    private fun setupHistoryAdapter(){
        historyAdapter = HistoryAdapter()
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.inner_text_block_spacing)
        val itemDecoration = SpacingItemDecoration(spacingInPixels)

        historyAdapter.setOnItemClickCallback {
            goToHistoryRecord(it)
        }

        binding.layerHistory.historyRecyclerview.adapter = historyAdapter

        binding.layerHistory.historyRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(itemDecoration)
        }

    }

    private fun goToHistoryRecord(historyEntry: HistoryEntry){
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        when(historyEntry.status){
            Status.Analyzing -> {
                mainViewModel.operationLoading(getString(R.string.the_track_is_being_analyzed), true)
            }
            Status.Deny -> {
                mainViewModel.operationError(getString(R.string.the_track_isn_t_analyzing_for_some_reason))
            }
            Status.Success -> {
                mainViewModel.loadRecommendationFromHistory(this@MainActivity, historyEntry.id)
            }
        }
    }

    private fun setupUserInfo(userData: UserData){
        binding.layerHistory.user.setUserInfo(userData)
    }

    private fun setCaption(caption: String){
        binding.caption.text = caption
    }

    private fun handleAnalyzeLoading(msg: String? = null, canLeaveScreen: Boolean = false){
        if (navController.currentDestination?.id != R.id.loadingFragment) {
            val action = LoadingFragmentDirections.actionGlobalToLoadingFragment(msg,canLeaveScreen)
            navController.navigate(action)
        }
    }

    private fun handleAnalyzeIdle(){
        if (navController.currentDestination?.id == R.id.loadingFragment) {
            navController.navigate(R.id.action_loadingFragment_to_searchMainFragment)
        }
    }

    private fun handleMusicAnalyzeSuccess(){
        if (navController.currentDestination?.id == R.id.loadingFragment) {
            navController.navigate(R.id.action_loadingFragment_to_recomedationsFragment)
        }
    }

    private fun handleMusicAnalyzeError(msg: String? = null) {
        val action = ErrorFragmentDirections
            .actionGlobalToErrorFragment(msg)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loadingFragment, true)
            .build()

        navController.navigate(action, navOptions)
    }

    override fun onSupportNavigateUp(): Boolean {
        return androidx.navigation.ui.NavigationUI.navigateUp(navController, null)
                || super.onSupportNavigateUp()
    }

    private fun goToAuthentication(){
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}