package com.savana.ui.fragments.main.search.main

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.savana.R
import com.savana.databinding.FragmentSearchMainBinding
import com.savana.ui.activities.main.MainViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchMainFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModel()
    private val searchMainViewModel: SearchMainViewModel by viewModel()

    private var _binding: FragmentSearchMainBinding? = null
    private val binding get() = _binding!!

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(it)
            searchMainViewModel.setFile(it, fileName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUiListeners()
        setupViewModelListeners()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.setCaption("Savana")
    }

    private fun setupViewModelListeners(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    searchMainViewModel.state.collect{ value ->
                        if (value.fileName!=null && value.uri != null){
                            gapSelectorScreen(value.uri, value.fileName)
                        }
                    }
                }

            }
        }
    }

    private fun gapSelectorScreen(uri: Uri, fileName: String) {
        val navController = findNavController()

        val action = SearchMainFragmentDirections
            .actionGlobalToGapSeelction(fileName, uri)

        navController.navigate(action)

        searchMainViewModel.onNavigationToGapSelectorDone()
        mainViewModel.clearError()
        mainViewModel.operationHandled()
    }

    private fun setupUiListeners(){
        binding.selectFileButton.setOnClickListener {
            filePickerLauncher.launch("audio/*")
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        fileName = it.getString(displayNameIndex)
                    }
                }
            }
        }
        if (fileName == null) {
            fileName = uri.path
            val cut = fileName?.lastIndexOf('/')
            if (cut != -1 && cut != null) {
                fileName = fileName?.substring(cut + 1)
            }
        }
        return fileName
    }
}