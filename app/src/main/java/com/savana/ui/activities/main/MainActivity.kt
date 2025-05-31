package com.savana.ui.activities.main

import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.savana.R
import com.savana.databinding.ActivityMainBinding
import com.savana.ui.fragments.main.search.loading.LoadingFragmentDirections
import com.savana.ui.fragments.main.search.selection.GapSelectorFragmentDirections
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModel()

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
                }

                override fun onDrawerClosed(drawerView: View) {
                    menuButton.isActivated = false
                }
            })
        }

        setupListeners()
    }

    private fun setupListeners(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    mainViewModel.recommendationResult.collect{ result ->
                        when(result){
                            is OperationState.Error -> handleMusicAnalyzeError()
                            is OperationState.Idle -> {}
                            is OperationState.Loading -> handleAnalyzeLoading()
                            is OperationState.Success<*> -> handleMusicAnalyzeSuccess()
                        }
                        mainViewModel.operationHandled()
                    }
                }

                launch {
                    mainViewModel.mainState.collect{ state ->
                        if (state.caption != null){
                            setCaption(state.caption)
                        }
                    }
                }
            }
        }
    }

    private fun setCaption(caption: String){
        binding.caption.text = caption
    }

    private fun handleMusicAnalyzeSuccess(){
        val action = LoadingFragmentDirections
            .actionLoadingFragmentToRecomedationsFragment()
        navController.navigate(action)
    }

    private fun handleMusicAnalyzeError(){
        val action = LoadingFragmentDirections
            .actionLoadingFragmentToGapSelectorFragment(null,null)
        navController.navigate(action)
    }

    private fun handleAnalyzeLoading(){
        val action = GapSelectorFragmentDirections
            .actionGapSelectorFragmentToLoadingFragment()
        navController.navigate(action)
    }

    override fun onSupportNavigateUp(): Boolean {
        return androidx.navigation.ui.NavigationUI.navigateUp(navController, null)
                || super.onSupportNavigateUp()
    }

}