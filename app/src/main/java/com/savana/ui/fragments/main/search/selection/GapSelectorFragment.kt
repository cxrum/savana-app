package com.savana.ui.fragments.main.search.selection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.savana.R
import com.savana.databinding.FragmentGapSelectorBinding
import com.savana.ui.activities.main.MainViewModel

class GapSelectorFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val gapSelectionViewModel: GapSelectionViewModel by activityViewModels()

    private var _binding: FragmentGapSelectorBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGapSelectorBinding.inflate(inflater, container, false)

        return binding.root
    }

}