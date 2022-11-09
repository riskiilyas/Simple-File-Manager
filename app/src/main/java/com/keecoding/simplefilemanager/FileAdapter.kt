package com.keecoding.simplefilemanager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.keecoding.simplefilemanager.databinding.FileItemLayoutBinding
import java.io.File
import java.util.*

class FileAdapter(
    private val context: Context,
    private var fullList: List<File>,
    private val callback: OnFileListener
    ) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

//    private lateinit var usedList: List<File>

    init {
        sortList()
    }

    private var usedList = fullList

    inner class FileViewHolder(private val binding: FileItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(file: File, position: Int) {
            binding.tvFileName.text = file.name
            binding.ivFileType.generateIcon(file)
            binding.tvLastModified.text = "Last Modified: ${Date(file.lastModified())}"

            binding.root.setOnClickListener {
                if (file.isDirectory) {
                    updateList(callback.onDirectoryOpen(position))
                } else {
                    callback.onFileOpen(file)
                }
            }

            binding.root.setOnLongClickListener {
                val popupMenu = PopupMenu(context, it)

                popupMenu.menu.add("Open")
                popupMenu.menu.add("Rename")
                popupMenu.menu.add("Share")
                popupMenu.menu.add("Delete")

                popupMenu.setOnMenuItemClickListener {
                    when(it.title) {
                        "Delete" -> {
                            val name = file.name
                            if (file.delete()) {
                                Toast.makeText(context, "Deleted $name", Toast.LENGTH_SHORT).show()
                                callback.onFileChanged()
                            }
                        }

                        "Rename" -> {
                            callback.onFileRename(file)
                        }

                        "Share" -> {
                            callback.onShareFile(file)
                        }

                        "Open" -> {
                            callback.onFileOpen(file)
                        }
                    }
                    true
                }

                popupMenu.show()
                true
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<File>) {
        fullList = list
        sortList()
        notifyDataSetChanged()
    }

    fun cancelSearch() {
        usedList = fullList
        notifyDataSetChanged()
    }

    fun search(str: String) {
        usedList = fullList.filter {
            it.name.contains(str, true)
        }
        notifyDataSetChanged()
    }

    private fun sortList() {
//        usedList = fullList.sortedWith (compareBy{it.name})
        usedList = fullList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = FileItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(usedList[position], position)
    }

    override fun getItemCount() = usedList.size

    interface OnFileListener {
        fun onDirectoryOpen(directoryPosition: Int): List<File>
        fun onFileOpen(file: File)
        fun onFileChanged(): List<File>
        fun onShareFile(file: File)
        fun onFileRename(file: File)
    }
}