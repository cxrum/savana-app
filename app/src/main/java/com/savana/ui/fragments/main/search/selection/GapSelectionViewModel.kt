package com.savana.ui.fragments.main.search.selection

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.savana.R
import com.savana.domain.models.SelectedTrackGap
import com.savana.domain.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class GapSelectionViewModel() : ViewModel() {

    private val _state = MutableStateFlow(GapSelectionState())
    val state: StateFlow<GapSelectionState> = _state.asStateFlow()

    private val _currentAudioBytes = MutableStateFlow(ByteArray(0))
    val currentAudioBytes: StateFlow<ByteArray> = _currentAudioBytes

    @UnstableApi
    fun loadAudioFile(uri: Uri, contentResolver: ContentResolver, context: Context){

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val buffer = ByteArrayOutputStream()
                    var nRead: Int
                    val data = ByteArray(1024)
                    while (inputStream.read(data, 0, data.size).also { nRead = it } != -1) {
                        buffer.write(data, 0, nRead)
                    }
                    buffer.flush()
                    _currentAudioBytes.value = buffer.toByteArray()

                } ?: run {

                    _state.value = _state.value.copy(isLoading = false, errorMessage = context.getString(
                        R.string.file_does_not_open
                    ))
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message ?: context.getString(
                    R.string.file_does_not_open
                ))
            }finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun getFileName(uri: Uri, context: Context): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
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

    fun setCover(cover: Bitmap?){
        _state.value = _state.value.copy(
            cover = cover
        )
    }

    fun setTitle(title: String?){
        _state.value = _state.value.copy(
            title = title
        )
    }

    fun setFilename(fileName: String?){
        _state.value = _state.value.copy(
            fileName = fileName
        )
    }

    fun showError(e: Exception){
        _state.value = _state.value.copy(errorMessage = e.message)
    }

    fun onRangeSelected(startSec: Int, endSec: Int) {
        _state.value = _state.value.copy(startSec = startSec, endSec = endSec)
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

}