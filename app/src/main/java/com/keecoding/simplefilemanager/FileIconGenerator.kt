package com.keecoding.simplefilemanager

import android.widget.ImageView
import java.io.File

fun ImageView.generateIcon(file: File) {
    if (file.isDirectory) {
        setImageResource(R.drawable.folder)
        return
    }
    when (file.extension.lowercase()) {
        "mp4", "3gp" -> {
            setImageResource(R.drawable.video)
        }

        "mp3", "wav" -> {
            setImageResource(R.drawable.audio)
        }

        "jpg", "jpeg", "png" -> {
            setImageResource(R.drawable.image)
        }

        "gif" -> {
            setImageResource(R.drawable.gif)
        }

        "pdf" -> {
            setImageResource(R.drawable.pdf)
        }

        "doc", "docx" -> {
            setImageResource(R.drawable.word)
        }

        "zip", "rar", "7z", "iso" -> {
            setImageResource(R.drawable.compress)
        }

        else -> {
            setImageResource(R.drawable.unknown)
        }
    }
}