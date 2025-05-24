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
import javax.inject.Inject

@HiltViewModel
class FileActionViewModel @Inject constructor(
    private val copyUseCase: CopyFileUseCase,
    private val cutUseCase: CutFileUseCase,
    private val deleteUseCase: DeleteFileUseCase,
    private val renameUseCase: RenameFileUseCase,
    private val infoUseCase: GetFileInfoUseCase
) : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    /**
     * Copia un elemento de [sourcePath] a [targetDirPath].
     */
    fun copy(sourcePath: String, targetDirPath: String) = viewModelScope.launch {
        val result = copyUseCase(sourcePath, targetDirPath)
        _status.value = if (result) "Copiado a $targetDirPath" else "Error al copiar"
    }

    /**
     * Corta (mueve) un elemento de [sourcePath] a [targetDirPath].
     */
    fun cut(sourcePath: String, targetDirPath: String) = viewModelScope.launch {
        val result = cutUseCase(sourcePath, targetDirPath)
        _status.value = if (result) "Movido a $targetDirPath" else "Error al mover"
    }

    /**
     * Borra permanentemente el elemento en [path].
     */
    fun delete(path: String) = viewModelScope.launch {
        val result = deleteUseCase(path)
        _status.value = if (result) "Borrado permanentemente" else "Error al borrar"
    }

    /**
     * Renombra el elemento en [oldPath] al nombre [newName].
     */
    fun rename(oldPath: String, newName: String) = viewModelScope.launch {
        val result = renameUseCase(oldPath, newName)
        _status.value = if (result) "Renombrado a $newName" else "Error al renombrar"
    }

    /**
     * Obtiene info detallada de [path] y la convierte a texto.
     */
    fun getInfo(path: String) = viewModelScope.launch {
        val info: FileInfo = infoUseCase(path)
        _status.value = info.toDisplayString()
    }
}
