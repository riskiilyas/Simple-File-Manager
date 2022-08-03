package com.keecoding.simplefilemanager

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.MimeTypeFilter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.keecoding.simplefilemanager.databinding.ActivityMainBinding
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity(), FileAdapter.OnFileListener {

    private lateinit var vm: FileViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAdapter: FileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this).get(FileViewModel::class.java)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFiles()
        binding.refreshLayout.setOnRefreshListener { setupFiles() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        menu?.let {
            val searchMenu = it.findItem(R.id.iSearch)
            val search = searchMenu.actionView as SearchView
            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    mAdapter.search(p0 ?: "")
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    mAdapter.search(p0 ?: "")
                    return true
                }
            })

            search.setOnCloseListener {
                mAdapter.cancelSearch()
                true
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun getFiles(): List<File> {
        return if (vm.fileList.isNullOrEmpty()) {
            val path = Environment.getExternalStorageDirectory().path
            val file = File(path)
            file.listFiles()?.asList() ?: listOf()
        } else {
            vm.fileList.peek()
        }
    }

    private fun setupFiles() {
        if (checkPermission()) {
            val files = getFiles()
            mAdapter = FileAdapter(this, files, this)
            binding.recyclerView.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        } else {
            requestPermission()
        }
        binding.refreshLayout.isRefreshing = false
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "Allow to write external storage manually on settings!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 123)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Can't continue, permission denied!", Toast.LENGTH_SHORT).show()
        } else if (requestCode == 123 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupFiles()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        if(vm.fileList.size > 1) {
            vm.fileList.pop()
            mAdapter.updateList(getFiles())
            binding.recyclerView.smoothScrollToPosition(vm.files.pop())
        } else {
            AlertDialog.Builder(this).apply {
                setTitle("Are You Sure Exit?")
                setPositiveButton("Exit") { _, _ ->
                    super.onBackPressed()
                }
                setNegativeButton("Cancel") { _, _ ->
                    setCancelable(true)
                }
                show()
            }
        }
    }

    override fun onDirectoryOpen(directoryPosition: Int): List<File> {
        return vm.openFolder(directoryPosition)
    }

    override fun onFileOpen(file: File) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val mime = MimeTypeMap.getSingleton()
            val typeCompat = mime.getMimeTypeFromExtension(file.extension)
            intent.setDataAndType(Uri.parse(file.absolutePath), typeCompat)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open the file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFileChanged(): List<File> {
        vm.fileList.pop()
        return vm.openFolder(vm.files.peek())
    }
}