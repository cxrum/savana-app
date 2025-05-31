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
import com.savana.databinding.FragmentNameBinding
import com.savana.ui.activities.registration.RegistrationViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NameFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModel()


    private var _binding: FragmentNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        initStateFromViewModel()
    }

    private fun initStateFromViewModel(){
        val nickname = registrationViewModel.state.value.nickname
        if (nickname != null){
            binding.nickname.setText(nickname)
        }
    }

    private fun setupListeners(){

        binding.nickname.doOnTextChanged { text, _, _, _ ->
            registrationViewModel.onNicknameChanged(text.toString(), requireContext())
        }

        binding.nickname.setOnEditorActionListener { textView, actionId, event ->
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
            val name = registrationViewModel.state.value.nickname
            if(name != null ){
                if (registrationViewModel.state.value.errorMessage == null){
                    registrationViewModel.userRequestsNextStep()
                } else if (name.isBlank()) {
                    registrationViewModel.setError(getString(R.string.nickname_cannot_be_empty))
                }
            }else{
                registrationViewModel.setError(getString(R.string.nickname_cannot_be_empty))
            }
        }
    }

    private fun setupObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    registrationViewModel.state.collect{ state ->
                        if (state.errorMessage != null){
                            setNameError(state.errorMessage)
                        }else{
                            setNameCorrect()
                        }
                    }
                }
            }
        }
    }

    private fun setNameError(error: String?){
        binding.errorMsg.text = error
        binding.errorMsg.setTextColor(requireContext().getColor(R.color.colorError))
    }

    private fun setNameCorrect(){
        binding.errorMsg.text = null
        binding.errorMsg.setTextColor(requireContext().getColor(R.color.colorSuccess))
    }
}