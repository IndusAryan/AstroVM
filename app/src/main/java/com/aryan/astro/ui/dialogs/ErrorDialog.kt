package com.aryan.astro.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import com.aryan.astro.helpers.CrashLogHelper
import com.aryan.astro.utils.ToastUtil.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ErrorDialog {
    fun errorDialog(context: Context) {
        val errorLog = CrashLogHelper.getErrorLog()
        val clipboardManager =
            context.getSystemService<ClipboardManager>()!!

        // reset the error log
        CrashLogHelper.saveErrorLog("")

        MaterialAlertDialogBuilder(context)
            .setTitle("Error occurred")
            .setMessage(errorLog)
            .setNegativeButton("Ok", null)
            .setPositiveButton("Copy") { _, _ ->
                clipboardManager.setPrimaryClip(ClipData.newPlainText("Title", errorLog))
                showToast(context,"Copied" )
            }
            .show()
    }
}
