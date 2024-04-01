package com.aryan.astro.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.aryan.astro.R
import com.aryan.astro.databinding.ActivityResultBinding
import com.google.android.material.appbar.MaterialToolbar
import java.io.ByteArrayOutputStream

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customTitleView = LayoutInflater.from(this).inflate(R.layout.custom_title_layout, null)
        val appBar: MaterialToolbar = findViewById(R.id.appBarLayout)
        appBar.addView(customTitleView)

        // Retrieve data from the intent
        val description = intent.getStringExtra("Description")
        val imageResource = intent.getIntExtra("ImageResource", 0)

        binding.description.text = description
        binding.signImage.setImageResource(imageResource)

        binding.back.setOnClickListener {
            finish()
            onBackPressedDispatcher.onBackPressed()
        }

        binding.share.setOnClickListener {

            val drawable: Drawable? = binding.signImage.drawable
            val bitmap: Bitmap? = drawable?.toBitmap()

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, "$description")
                putExtra(Intent.EXTRA_STREAM, getImageUriFromBitmap(bitmap))
               type = "image/*"
        }

            val chooserIntent =
                Intent.createChooser(shareIntent, "Share with")
            startActivity(chooserIntent)
        }

    }

    @Suppress("DEPRECATION")
    private fun getImageUriFromBitmap(bitmap: Bitmap?): Uri? {

        if (bitmap != null) {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, bytes)
            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
            return Uri.parse(path)
        }

        return null
    }
}
