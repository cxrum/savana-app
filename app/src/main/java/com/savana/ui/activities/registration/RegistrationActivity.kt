package com.savana.ui.activities.registration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import com.savana.R
import com.savana.databinding.ActivityRegistrationBinding
import com.savana.ui.activities.authentication.AuthenticationActivity
import com.savana.ui.activities.main.MainActivity
import com.savana.ui.activities.registration.RegistrationViewModel.Companion.Steps
import com.savana.ui.fragments.registration.EmailFragmentDirections
import com.savana.ui.fragments.registration.NameFragmentDirections
import com.savana.ui.fragments.registration.PasswordFragmentDirections
import com.savana.ui.fragments.registration.avatar.SelectAvatarFragmentDirections
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationActivity : AppCompatActivity(){

    private var _binding: ActivityRegistrationBinding? = null
    private val binding get() = _binding!!

    private val registrationViewModel: RegistrationViewModel by viewModel()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.navHostFragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController

        setupListeners()
        setupObservers()
    }

    private fun setupListeners(){
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                registrationViewModel.userRequestsPreviousStep()
            }
        })

        binding.goBack.setOnClickListener {
            registrationViewModel.userRequestsPreviousStep()
        }

    }

    private fun setupObservers() {
        registrationViewModel.authEventLiveData.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                goToAuthentication()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    registrationViewModel.navigateToStepEvent.collect { event ->
                        event?.getContentIfNotHandled()?.let { stepToNavigateTo ->
                            val currentActualStep = registrationViewModel.currentStepLiveData.value

                            if (currentActualStep!=null){
                                val action = getNavigationAction(currentActualStep, stepToNavigateTo)

                                if (stepToNavigateTo == Steps.REGISTERED) {
                                    registrationViewModel.register(this@RegistrationActivity)
                                    registrationViewModel.startLoading()
                                    return@let
                                }

                                if (action != null) {
                                    navController.navigate(action)
                                    registrationViewModel.stepChanged(stepToNavigateTo)
                                } else if (stepToNavigateTo == Steps.WELCOME && currentActualStep == Steps.AVATAR) {
                                    registrationViewModel.stepChanged(stepToNavigateTo)
                                }
                            }
                        }
                    }
                }

                launch {
                    registrationViewModel.currentStepLiveData.collect { state ->
                        if (state != null){
                            if (state == Steps.EMAIL){
                                setTopBarVisibility(View.GONE)
                            }else{
                                setTopBarVisibility(View.VISIBLE)
                                setStepNumber(state.stepOrder)
                            }
                        }
                    }
                }

                launch {
                    registrationViewModel.state.collect{ state ->
                        if(state.success != null){
                            if (state.success){
                                registrationViewModel.stopLoading()
                                goToMain()
                            }else{
                                goToAuthentication()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun getNavigationAction(currentStep: Steps, targetStep: Steps): NavDirections? {
        return when (currentStep) {
            Steps.EMAIL -> if (targetStep == Steps.NAME) EmailFragmentDirections.actionEmailFragmentToNameFragment() else null
            Steps.NAME -> when (targetStep) {
                Steps.PASSWORD -> NameFragmentDirections.actionNameFragmentToPasswordFragment()
                Steps.EMAIL -> NameFragmentDirections.actionNameFragmentToEmailFragment()
                else -> null
            }
            Steps.PASSWORD -> when (targetStep) {
                Steps.AVATAR -> PasswordFragmentDirections.actionPasswordFragmentToAvatarFragment()
                Steps.NAME -> PasswordFragmentDirections.actionPasswordFragmentToNameFragment()
                else -> null
            }
            Steps.AVATAR -> when (targetStep) {
                Steps.WELCOME -> SelectAvatarFragmentDirections.actionAvatarFragmentToWelcomeFragment()
                Steps.PASSWORD -> SelectAvatarFragmentDirections.actionAvatarFragmentToPasswordFragment()
                else -> null
            }
            Steps.WELCOME -> when (targetStep) {
                Steps.REGISTERED -> null
                else -> null
            }
            Steps.REGISTERED -> null
        }
    }

    private fun setStepNumber(step: Int){
        binding.stepBar.setCurrentStep(step)
    }

    private fun setTopBarVisibility(visibility: Int){
        binding.tobBlock.visibility = visibility
    }

    private fun goToAuthentication(){
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun goToMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}