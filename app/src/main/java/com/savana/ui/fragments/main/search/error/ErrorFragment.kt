package com.savana.ui.fragments.main.search.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.savana.databinding.FragmentErrorBinding

class ErrorFragment : Fragment() {

    private var _binding: FragmentErrorBinding? = null
    private val binding get() = _binding!!

    val args: ErrorFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.msg!=null){
            setupErrorMsg(args.msg!!)
        }
    }

    private fun setupErrorMsg(msg: String){
        binding.errorMsg.text = msg
    }
}