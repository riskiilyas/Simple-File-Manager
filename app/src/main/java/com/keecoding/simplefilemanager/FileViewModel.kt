package com.keecoding.simplefilemanager

import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class FileViewModel: ViewModel() {

    val fileList: Stack<List<File>> = Stack()
    val files: Stack<String> = Stack()

    fun openFolder(folder: String): List<File> {
        val file = File(folder)
        files.push(folder)
        return fileList.push(file.listFiles().toList())
    }
}