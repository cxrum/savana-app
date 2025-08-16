package com.savana.ui.fragments.main.search.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.savana.databinding.FragmentLoadingBinding
import com.savana.ui.activities.main.MainViewModel
import kotlinx.coroutines.launch

class LoadingFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    private val args: LoadingFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        mainViewModel.setCaption("")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    mainViewModel.mainState.collect { state ->
                        if (state.canLeaveLoadingScreen){
                            canLeave()
                        }else{
                            notCanLeave()
                        }
                    }
                }

            }
        }

        if (args.msg != null){
            setUserMessage(args.msg!!)
        }

    }

    private fun setUserMessage(msg: String){
        binding.message.text = msg
    }

    private fun canLeave(){
        binding.leaveScreenLabel.visibility = View.VISIBLE
    }

    private fun notCanLeave(){
        binding.leaveScreenLabel.visibility = View.GONE
    }

}