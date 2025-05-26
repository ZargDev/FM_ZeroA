package com.zalune.fm_zeroa.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zalune.fm_zeroa.domain.model.CloudFile
import com.zalune.fm_zeroa.domain.repository.usecase.CDeleteFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.CDownloadFileUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.CListFilesUseCase
import com.zalune.fm_zeroa.domain.repository.usecase.CUploadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CloudFilesViewModel @Inject constructor(
    private val uploadUC: CUploadFileUseCase,
    private val downloadUC: CDownloadFileUseCase,
    private val listUC: CListFilesUseCase,
    private val deleteUC: CDeleteFileUseCase
) : ViewModel() {

    private val _files = MutableLiveData<List<CloudFile>>()
    val files: LiveData<List<CloudFile>> = _files

    private val _progress = MutableLiveData<Float>()
    val progress: LiveData<Float> = _progress

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        refreshList()
    }

    fun refreshList() {
        viewModelScope.launch {
            listUC().fold(
                onSuccess = { _files.value = it },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun upload(filePath: String) {
        viewModelScope.launch {
            uploadUC(filePath).collect { result ->
                result.onSuccess { prog ->
                    _progress.value = prog
                    if (prog == 1.0f) refreshList()
                }.onFailure { _error.value = it.message }
            }
        }
    }

    fun download(cloudFile: CloudFile) {
        viewModelScope.launch {
            downloadUC(cloudFile).collect { result ->
                result.onSuccess { prog ->
                    _progress.value = prog
                }.onFailure { _error.value = it.message }
            }
        }
    }

    fun delete(cloudFile: CloudFile) {
        viewModelScope.launch {
            deleteUC(cloudFile).fold(
                onSuccess = { refreshList() },
                onFailure = { _error.value = it.message }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }
}
