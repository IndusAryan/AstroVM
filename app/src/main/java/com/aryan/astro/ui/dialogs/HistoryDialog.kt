package com.aryan.astro.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.aryan.astro.db.DataStoreHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HistoryDialog(private val historyList: List<String>) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sign History")
            .setItems(historyList.toTypedArray()) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Ok", null)
            .setNegativeButton("Clear") { _, _ ->
                clearHistory()
            }
            .show()
    }

    private fun clearHistory() {
        DataStoreHelper(requireContext()).clearData()
    }
}
