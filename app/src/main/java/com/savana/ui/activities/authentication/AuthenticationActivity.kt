package com.savana.ui.activities.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.savana.R
import com.savana.ui.activities.registration.RegistrationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationActivity : AppCompatActivity(){

    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }

    private fun goToRegistration(){
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}