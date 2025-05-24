package com.zalune.fm_zeroa.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CloudFilesViewModel @Inject constructor() : ViewModel() {
    private val _testCounter = MutableLiveData(0)
    val testCounter: LiveData<Int> = _testCounter

    fun incrementCounter() {
        _testCounter.value = _testCounter.value?.plus(1)
    }
}