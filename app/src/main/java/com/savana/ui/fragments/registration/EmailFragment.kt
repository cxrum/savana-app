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
import com.savana.databinding.FragmentEmailBinding
import com.savana.ui.activities.registration.RegistrationViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EmailFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModel()

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!

    private var interactedWithEditText: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        initStateFromViewModel()
    }

    private fun initStateFromViewModel(){
        val email = registrationViewModel.state.value.email
        if (email != null){
            binding.emailInput.setText(email)
        }
    }

    private fun setupListeners(){
        binding.emailInput.doOnTextChanged { text, _, _, _ ->
            interactedWithEditText = true
            registrationViewModel.onEmailChanged(text.toString(), requireContext())
        }

        binding.emailInput.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                interactedWithEditText = true
                requireActivity().hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.cancel.setOnClickListener {
            registrationViewModel.goToAuthentication()
        }

        binding.confirmButton.setOnClickListener {
            if (registrationViewModel.state.value.errorMessage == null
                && registrationViewModel.state.value.email != null){
                registrationViewModel.userRequestsNextStep()
            } else if (registrationViewModel.state.value.email != null && registrationViewModel.state.value.email!!.isBlank()) {
                registrationViewModel.setError(getString(R.string.email_cannot_be_empty))
            }
        }
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    registrationViewModel.state.collect{ state ->
                        if(interactedWithEditText){
                            if (state.errorMessage != null){
                                setEmailError(state.errorMessage)
                            }else{
                                setEmailCorrect()
                            }
                        }else{
                            setEmailError(null)
                        }
                    }
                }
            }
        }
    }

    private fun setEmailError(error: String?){
        binding.errorMsg.text = error
        binding.errorMsg.setTextColor(requireContext().getColor(R.color.colorError))
    }

    private fun setEmailCorrect(){
        binding.errorMsg.text = null
        binding.errorMsg.setTextColor(requireContext().getColor(R.color.colorSuccess))
    }

}