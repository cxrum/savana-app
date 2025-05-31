package com.savana.ui.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.savana.databinding.FragmentSelectAvatarBinding
import com.savana.ui.activities.registration.RegistrationViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectAvatarFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModel()

    private var _binding: FragmentSelectAvatarBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSelectAvatarBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners(){
        binding.confirmButton.setOnClickListener {
            if (registrationViewModel.state.value.errorMessage == null){
                registrationViewModel.userRequestsNextStep()
            }
        }
    }
}