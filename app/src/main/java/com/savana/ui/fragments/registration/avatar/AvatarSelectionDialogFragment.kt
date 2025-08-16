package com.savana.ui.fragments.registration.avatar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.savana.R
import com.savana.databinding.DialogAvatarSelectionBinding
import com.savana.ui.activities.registration.RegistrationViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AvatarSelectionDialogFragment : DialogFragment() {

    private var _binding: DialogAvatarSelectionBinding? = null
    private val binding get() = _binding!!

    private var selectedAvatarId: Int? = null

    private val registrationViewModel: RegistrationViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAvatarSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialogAppearance()
        setupAvatarGrid()
        setupClickListeners()
    }


    private fun setupDialogAppearance() {

        dialog?.window?.apply {
            setBackgroundDrawable(getDrawable(requireContext(), R.drawable.dialog_background))

            val displayMetrics = requireContext().resources.displayMetrics
            val marginDp = 16
            val marginPx = (marginDp * displayMetrics.density).toInt()

            val newWidth = displayMetrics.widthPixels - (2 * marginPx)
            setLayout(newWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun setupAvatarGrid() {
        val avatarList = registrationViewModel.avatars.value
        val adapter = AvatarListAdapter(requireContext(), avatarList ?: emptyList())
        binding.avatarGridView.adapter = adapter

        binding.avatarGridView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                selectedAvatarId = adapter.getItem(position)?.id
            }
    }

    private fun setupClickListeners() {
        binding.goBack.setOnClickListener {
            dismiss()
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            selectedAvatarId?.let { id ->
                setFragmentResult("avatar_selection_request", bundleOf("selected_avatar" to id))
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AvatarSelectionDialog"
    }
}