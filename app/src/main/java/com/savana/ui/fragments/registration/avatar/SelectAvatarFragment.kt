package com.savana.ui.fragments.registration.avatar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import coil.load
import coil.transform.CircleCropTransformation
import com.savana.R
import com.savana.databinding.FragmentSelectAvatarBinding
import com.savana.ui.activities.registration.RegistrationViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SelectAvatarFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModel()

    private var _binding: FragmentSelectAvatarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialogListener()
        setupListeners()

        val avatarUrl = registrationViewModel.getRandomAvatar()

        binding.avatarSelect.load(avatarUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_image_placeholder)
            error(R.drawable.ic_image_placeholder)
            transformations(CircleCropTransformation())
        }
    }

    private fun setupDialogListener() {
        childFragmentManager.setFragmentResultListener(
            "avatar_selection_request",
            this
        ) { requestKey, bundle ->
            val selectedId = bundle.getInt("selected_avatar")
            registrationViewModel.onAvatarIdChanged(selectedId, requireContext())
        }
    }


    private fun setupListeners(){
        binding.confirmButton.setOnClickListener {
            if (registrationViewModel.state.value.errorMessage == null){
                registrationViewModel.userRequestsNextStep()
            }
        }

        binding.avatarSelect.setOnClickListener {
            AvatarSelectionDialogFragment().show(childFragmentManager, AvatarSelectionDialogFragment.TAG)
        }

    }
}