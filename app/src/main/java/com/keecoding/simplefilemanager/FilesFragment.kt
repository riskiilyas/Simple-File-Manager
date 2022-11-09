package com.keecoding.simplefilemanager

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.keecoding.simplefilemanager.databinding.FragmentFilesBinding
import java.io.File

class FilesFragment : Fragment(), FileAdapter.OnFileListener {
    private var _b: FragmentFilesBinding? = null
    private val b get() = _b!!
    private var path: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _b = FragmentFilesBinding.inflate(layoutInflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            path = it.getString("path", Environment.getExternalStorageDirectory().path)
        }

        val files = File(path!!).listFiles()
    }

    override fun onDirectoryOpen(directoryPath: String) {
        val action = FilesFragmentDirections.actionFilesFragmentSelf(directoryPath)
        findNavController().navigate(action)
    }

    override fun onFileOpen(file: File) {
        TODO("Not yet implemented")
    }

    override fun onShareFile(file: File) {
        TODO("Not yet implemented")
    }

    override fun onFileRename(file: File) {
        TODO("Not yet implemented")
    }
}