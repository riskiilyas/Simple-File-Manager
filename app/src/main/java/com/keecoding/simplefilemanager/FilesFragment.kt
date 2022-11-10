package com.keecoding.simplefilemanager

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.keecoding.simplefilemanager.databinding.FragmentFilesBinding
import java.io.File

class FilesFragment : Fragment(), FileAdapter.OnFileListener {
    private var _b: FragmentFilesBinding? = null
    private val b get() = _b!!
    private var path: String? = null
    private lateinit var viewModel: FileViewModel

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

        viewModel = (activity as MainActivity).getViewModel()

        viewModel.searchQuery.observe(viewLifecycleOwner) {
            if (it==null) {

            } else {

            }
        }

        arguments?.let {
            path = it.getString("path", Environment.getExternalStorageDirectory().path)
        }

        val files = File(path!!).listFiles()?.asList() ?: emptyList()
        println(files.size.toString() + " " + path)
        val mAdapter = FileAdapter(requireContext(), files, this)
        b.recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        b.refreshLayout.setOnRefreshListener {
            val files = File(path!!).listFiles()?.asList() ?: emptyList()

        }
    }

    override fun onDirectoryOpen(directoryPath: String) {
        val action = FilesFragmentDirections.actionFilesFragmentSelf(directoryPath)
        action.path = directoryPath
        findNavController().navigate(action)
    }

    override fun onFileOpen(file: File) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val mime = MimeTypeMap.getSingleton()
            val typeCompat = mime.getMimeTypeFromExtension(file.extension)
            Log.d("aaaa", file.absolutePath)
//            intent.setDataAndType(Uri.parse(file.absolutePath), typeCompat)
            intent.setDataAndType(
                FileProvider.getUriForFile(requireContext(),
                "${ContactsContract.Directory.PACKAGE_NAME}.fileprovider", file), typeCompat)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Cannot open the file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onShareFile(file: File) {
        val intentShare = Intent()
        intentShare.action = Intent.ACTION_SEND
        val mime = MimeTypeMap.getSingleton()
        val typeCompat = mime.getMimeTypeFromExtension(file.extension)
        intentShare.setDataAndType(Uri.parse(file.absolutePath), typeCompat)
        intentShare.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intentShare)    }

    override fun onFileRename(file: File) {
        TODO("Not yet implemented")
    }
}