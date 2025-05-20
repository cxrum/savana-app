package com.savana.ui.fragments.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.savana.R
import com.savana.databinding.ActivityMainBinding
import com.savana.databinding.FragmentEmailBinding
import com.savana.ui.activities.main.MainViewModel
import com.savana.ui.activities.registration.RegistrationViewModel

class EmailFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModels()

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEmailBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {

    }
}