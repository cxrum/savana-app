package com.savana.ui.activities.authentication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.savana.R
import com.savana.ui.activities.registration.RegistrationActivity
import com.savana.ui.splash.StartupViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.savana.ui.activities.main.MainActivity
import com.savana.ui.splash.NavigationTarget
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthenticationActivity : AppCompatActivity(){

    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private val startupViewModel: StartupViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: NavigationTarget = startupViewModel.navigationTarget.value

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                startupViewModel.navigationTarget.collect { target ->
                    uiState = target
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                is NavigationTarget.Loading -> true
                is NavigationTarget.LoggedIn -> {
                    goToMain()
                    false
                }
                is NavigationTarget.NotLoggedIn -> false
            }
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_authentication)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()
    }

    private fun setupObservers(){
        authenticationViewModel.eventLiveData.observe(this){
            goToRegistration()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){

                launch {
                    authenticationViewModel.state.collect{ state ->
                        if (state.success != null){
                            if (state.success){
                                authenticationViewModel.stopLoading()
                                goToMain()
                            }else{
                                // TODO() Show error msg about unsuccessful auth
                            }
                        }
                    }
                }
            }
        }
    }


    private fun goToRegistration(){
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}