package com.zalune.fm_zeroa.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalune.fm_zeroa.domain.model.FileInfo
import com.zalune.fm_zeroa.domain.repository.usecase.CopyFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.CutFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.DeleteFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.GetFileInfoUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.RenameFileUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.toString

@HiltViewModel
class FileActionViewModel @Inject constructor(
    private val copyUseCase: CopyFileUseCase,
    private val cutUseCase: CutFileUseCase,
    private val deleteUseCase: DeleteFileUseCase,
    private val infoUseCase: GetFileInfoUseCase
) : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status


    private val _clipboard = MutableLiveData<ClipboardState>(ClipboardState.None)


    fun delete(path: String) {
        viewModelScope.launch {
            val ok = deleteUseCase(path)
            _status.postValue(if (ok) "Eliminado con éxito" else "Error al eliminar")
        }
    }

    fun rename(path: String, newName: String) {
        viewModelScope.launch {
            val dir = File(path).parentFile ?: run {
                _status.postValue("Error al renombrar")
                return@launch
            }
            val oldFile = File(path)
            val newFile = File(dir, newName)
            val ok = oldFile.renameTo(newFile)
            _status.postValue(if (ok) "Renombrado con éxito" else "Error al renombrar")
        }
    }

    fun getInfo(path: String) {
        viewModelScope.launch {
            val info = infoUseCase(path)
            _status.postValue(info.toString())
        }
    }
}
