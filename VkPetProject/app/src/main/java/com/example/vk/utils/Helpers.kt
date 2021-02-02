package com.example.vk.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
import com.example.vk.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Helpers {

    fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(context: Context, view: View) {
        val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    fun screenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun saveImageToCache(context: Context, image: Bitmap): Uri? {
        try {
            val cachePath = File(context.cacheDir, context.getString(R.string.cache_path))
            cachePath.mkdirs()
            val stream =
                    FileOutputStream("$cachePath/image.png")
            image.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            val newFile = File(cachePath, context.getString(R.string.file_name))
            return FileProvider.getUriForFile(
                    context,
                    context.getString(R.string.authority),
                    newFile
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun shareImage(activity: Activity, image: Bitmap) {
        val uri = saveImageToCache(activity, image)
        uri?.let {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.setDataAndType(uri, activity.contentResolver.getType(uri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            activity.startActivity(
                    Intent.createChooser(
                            shareIntent,
                            activity.getString(R.string.choosing_title)
                    )
            )
        }
    }
}