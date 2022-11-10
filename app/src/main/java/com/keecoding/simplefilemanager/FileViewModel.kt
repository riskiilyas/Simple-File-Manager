package com.keecoding.simplefilemanager

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class FileViewModel: ViewModel() {

    private val _searchQuery: MutableLiveData<String?> = MutableLiveData(null)
    val searchQuery get() = _searchQuery as LiveData<String?>

    fun search(query: String) {
        _searchQuery.postValue(query)
    }

    fun endSearch() {
        _searchQuery.postValue(null)
    }
}