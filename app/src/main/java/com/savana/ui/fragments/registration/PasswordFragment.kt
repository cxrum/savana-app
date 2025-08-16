package com.savana.ui.fragments.registration

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.savana.R
import com.savana.core.extension.hideKeyboard
import com.savana.databinding.FragmentPasswordBinding
import com.savana.ui.activities.registration.RegistrationViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PasswordFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModel()


    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        initStateFromViewModel()
    }

    private fun initStateFromViewModel(){
        val password = registrationViewModel.state.value.password
        if (password != null){
            binding.password.setText(password)
        }
    }


    private fun setupListeners(){

        binding.password.doOnTextChanged { text, _, _, _ ->
            registrationViewModel.onPasswordChanged(text.toString(), requireContext())
        }

        binding.password.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                requireActivity().hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.confirmButton.setOnClickListener {
            if (registrationViewModel.state.value.errorMessage == null){
                registrationViewModel.userRequestsNextStep()
            } else if (registrationViewModel.state.value.password != null && registrationViewModel.state.value.password!!.isBlank()) {
                registrationViewModel.setError(getString(R.string.password_cannot_be_empty))
            }
        }
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    registrationViewModel.state.collect{ state ->
                        if (state.errorMessage != null){
                            setPasswordError(state.errorMessage)
                        }else{
                            setPasswordCorrect()
                        }
                    }
                }
            }
        }
    }

    private fun setPasswordError(error: String?){
        binding.errorMsg.text = error
        binding.errorMsg.setTextColor(requireContext().getColor(R.color.colorError))
    }

    private fun setPasswordCorrect(){
        binding.errorMsg.text = null
        binding.errorMsg.setTextColor(requireContext().getColor(R.color.colorSuccess))
    }
}