package com.keecoding.simplefilemanager

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.Directory.PACKAGE_NAME
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
import androidx.core.content.FileProvider
import androidx.core.content.MimeTypeFilter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.keecoding.simplefilemanager.databinding.ActivityMainBinding
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var vm: FileViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[FileViewModel::class.java]
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFiles()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        menu?.let {
            val searchMenu = it.findItem(R.id.iSearch)
            val search = searchMenu.actionView as SearchView
            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return true
                }
            })

            search.setOnCloseListener {
                true
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupFiles() {
        if (!checkPermission()) {
            requestPermission()
        }
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

    fun getViewModel() = vm


//
//    override fun onBackPressed() {
//        if (vm.fileList.size > 1) {
//            vm.fileList.pop()
//            mAdapter.updateList(getFiles())
//            binding.recyclerView.smoothScrollToPosition(vm.files.pop())
//        } else {
//            AlertDialog.Builder(this).apply {
//                setTitle("Are You Sure Exit?")
//                setPositiveButton("Exit") { _, _ ->
//                    super.onBackPressed()
//                }
//                setNegativeButton("Cancel") { _, _ ->
//                    setCancelable(true)
//                }
//                show()
//            }
//        }
//    }
}