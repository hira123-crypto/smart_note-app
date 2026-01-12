package com.example.smartnotes.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.example.smartnotes.R

object UiUtils {

    fun showSuccessSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(view.context.getColor(R.color.success))
            .setTextColor(view.context.getColor(R.color.white))
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

    fun showErrorSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(view.context.getColor(R.color.error))
            .setTextColor(view.context.getColor(R.color.white))
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .setAction("Dismiss") { }
            .show()
    }

    fun showInfoSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(view.context.getColor(R.color.primary))
            .setTextColor(view.context.getColor(R.color.white))
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }
}