package com.uvk.shramapplication.helper

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.uvk.shramapplication.R
import com.github.chrisbanes.photoview.PhotoView

object ZoomImageUtils {
    fun showZoomableImage(context: Context, imageUrl: String) {
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_zoom_image)
        dialog.window?.attributes?.windowAnimations = android.R.style.Animation_Dialog


        val photoView = dialog.findViewById<PhotoView>(R.id.photoView)
        val closeBtn = dialog.findViewById<ImageView>(R.id.ivClose)
        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        Glide.with(context)
            .load(imageUrl)
            .into(photoView)

        dialog.show()
    }


    fun openPdfFile(context: Context, pdfUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(pdfUrl), "application/pdf")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

}


/*object ZoomImageUtils {
    fun showZoomableImage(context: Context, imageUrl: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_zoom_image)
        val photoView = dialog.findViewById<PhotoView>(R.id.photoView)

        // Load image using Glide
        Glide.with(context).load(imageUrl).into(photoView)

        dialog.show()
    }
}*/
