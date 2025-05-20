package com.savana.ui.fragments.main.search.loading

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.savana.R
import com.savana.databinding.FragmentLoadingBinding
import com.savana.ui.activities.main.MainViewModel

class LoadingFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()


    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoadingBinding.inflate(inflater, container, false)

        return binding.root
    }

}