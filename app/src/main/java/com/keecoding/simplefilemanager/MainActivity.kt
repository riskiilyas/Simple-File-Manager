package com.keecoding.simplefilemanager

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
            Toast.makeText(this, "${files.size}", Toast.LENGTH_SHORT).show()
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
        try {
            Log.d("aaaa", "onBackPressed: ${vm.fileList.size}")
            vm.fileList.pop()
            vm.files.pop()
            mAdapter.updateList(getFiles())
        } catch (e: EmptyStackException) {
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

    override fun onDirectoryOpen(directory: String): List<File> {
        return vm.openFolder(directory)
    }

    override fun onFileOpen(file: File) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            val type = "image/*"
            intent.setDataAndType(Uri.parse(file.absolutePath), type)
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