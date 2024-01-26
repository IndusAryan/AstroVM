package com.aryan.astro.helpers

import android.content.Context
import android.content.Intent
import com.aryan.astro.activities.ResultActivity

class IntentHelper {

     fun showResultActivity(context: Context, description: String, imageResource: Int) {
        // Create an Intent to start a new activity
        val intent = Intent(context, ResultActivity::class.java)

        // Pass data to the new activity using extras
        intent.putExtra("Description", description)
        intent.putExtra("ImageResource", imageResource)

        // Start the new activity
        context.startActivity(intent)
    }
}