package com.keecoding.simplefilemanager

import android.os.Environment
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class FileViewModel: ViewModel() {

    val fileList: Stack<List<File>> = Stack()
    val files: Stack<Int> = Stack()

    init {
        fileList.push(File(Environment.getExternalStorageDirectory().path).listFiles().asList())

        files.push(0)
    }

    fun openFolder(folderPosition: Int): List<File> {
        val file = File(fileList.peek()[folderPosition].path)
        files.push(folderPosition)
        return fileList.push(file.listFiles().toList())
    }
}