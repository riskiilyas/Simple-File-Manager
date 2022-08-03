package com.keecoding.simplefilemanager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.keecoding.simplefilemanager.databinding.FileItemLayoutBinding
import java.io.File

class FileAdapter(
    private val context: Context,
    private var list: List<File>,
    private val callback: OnFileListener
    ) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    inner class FileViewHolder(private val binding: FileItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.tvFileName.text = file.name

            binding.root.setOnClickListener {
                if (file.isDirectory) {
                    updateList(callback.onDirectoryOpen(file.path))
                } else {
                    callback.onFileOpen(file)
                }
            }

            binding.root.setOnLongClickListener {
                val popupMenu = PopupMenu(context, it)

                popupMenu.menu.add("Delete")
                popupMenu.menu.add("Remove")
                popupMenu.menu.add("Edit")

                popupMenu.setOnMenuItemClickListener {
                    when(it.title) {
                        "Delete" -> {
                            val name = file.name
                            if (file.delete()) {
                                Toast.makeText(context, "Deleted $name", Toast.LENGTH_SHORT).show()
                                callback.onFileChanged()
                            }
                        }

                        "Remove" -> {

                        }

                        "Edit" -> {

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
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = FileItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    interface OnFileListener {
        fun onDirectoryOpen(directory: String): List<File>
        fun onFileOpen(file: File)
        fun onFileChanged(): List<File>
    }
}