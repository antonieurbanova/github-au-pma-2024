package com.example.myapp011imagetoapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp011imagetoapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Inicializace pro viewBinding
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val getContent =
                registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                    uri?.let {
                        val bitmap = getBitmapFromUri(uri)
                        val blackAndWhiteBitmap = convertToBlackAndWhite(bitmap)
                        binding.ivImage.setImageBitmap(blackAndWhiteBitmap)
                    }
                }

            binding.btnTakeImage.setOnClickListener {
                getContent.launch("image/*")
            }
        }
            // Načtení Bitmap z URI
            private fun getBitmapFromUri(uri: Uri): Bitmap {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = android.graphics.ImageDecoder.createSource(contentResolver, uri)
                    android.graphics.ImageDecoder.decodeBitmap(source)
                } else {
                    contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    } ?: throw IllegalArgumentException("Unable to load image")
                }
            }
    private fun convertToBlackAndWhite(originalBitmap: Bitmap): Bitmap {
        // Vytvoření nové bitmapy s konfigurací ARGB_8888, pokud původní není upravitelná
        val mutableBitmap = if (!originalBitmap.isMutable || originalBitmap.config == null) {
            originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            originalBitmap
        }

        val width = mutableBitmap.width
        val height = mutableBitmap.height

        // Vytvoření nové bitmapy pro černobílý výsledek
        val blackAndWhiteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = android.graphics.Canvas(blackAndWhiteBitmap)
        val paint = android.graphics.Paint()

        // Nastavení ColorMatrix pro černobílý efekt
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // Nastavení sytosti na 0

        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter

        // Kreslení černobílé bitmapy na plátno
        canvas.drawBitmap(mutableBitmap, 0f, 0f, paint)

        return blackAndWhiteBitmap
    }

}