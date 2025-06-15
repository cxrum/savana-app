package com.savana.ui.fragments.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.savana.databinding.FragmentAuthenticationBinding
import com.savana.ui.activities.authentication.AuthenticationViewModel
import kotlinx.coroutines.launch

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
        setupObservers()
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){

                launch {
                    authenticationViewModel.state.collect{ state ->

                        if (state.errorMessage!=null){
                            setError(state.errorMessage)
                        }

                        if (state.isLoading){
                            showLoading()
                        }else{
                            disableLoading()
                        }
                    }
                }
            }
        }
    }

    private fun setError(msg: String?){
        binding.errorMsg.text = msg ?: ""
    }

    private fun showLoading(){
       binding.loading.visibility = View.VISIBLE
    }

    private fun disableLoading(){
        binding.loading.visibility = View.GONE
    }

    private fun setupListeners(){
        binding.registration.setOnClickListener {
            authenticationViewModel.clearErrorMsg()
            authenticationViewModel.goToRegistration()
        }

        binding.email.doOnTextChanged { text, _, _, _ ->
            authenticationViewModel.setEmail(text.toString())
        }
        binding.password.doOnTextChanged { text, _, _, _ ->
            authenticationViewModel.setPassword(text.toString())
        }

        binding.login.setOnClickListener {
            authenticationViewModel.login(this.requireContext())
        }
    }
}