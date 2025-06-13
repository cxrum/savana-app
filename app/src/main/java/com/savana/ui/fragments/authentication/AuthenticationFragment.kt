package com.savana.ui.fragments.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.savana.R
import com.savana.databinding.FragmentAuthenticationBinding
import com.savana.ui.activities.authentication.AuthenticationViewModel

class AuthenticationFragment : Fragment() {

    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners(){
        binding.registration.setOnClickListener {
            authenticationViewModel.goToRegistration()
        }

        binding.login.setOnClickListener {
            authenticationViewModel.login()
            authenticationViewModel.startLoading()
        }
    }
}