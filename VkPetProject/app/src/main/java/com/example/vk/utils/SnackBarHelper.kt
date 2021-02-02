package com.example.vk.utils

import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

object SnackBarHelper {
    fun showSnackBar(view: View, text: String, background: Int, image: Int) {
        val snackBar = Snackbar.make(
                view,
                text,
                Snackbar.LENGTH_SHORT
        )
        val textView =
                snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(image, 0, 0, 0)
        textView.compoundDrawablePadding = 30
        snackBar.view.setBackgroundResource(background)
        snackBar.show()
    }
}