package com.uvk.shramapplication.helper

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun savePdfToStorage(context: Context, fileName: String, inputStream: InputStream): File {
    val file = File(context.getExternalFilesDir(null), fileName)
    val outputStream = FileOutputStream(file)

    inputStream.copyTo(outputStream)

    outputStream.close()
    inputStream.close()

    return file
}
