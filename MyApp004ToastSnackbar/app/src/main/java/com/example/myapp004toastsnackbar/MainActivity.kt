package com.example.myapp004toastsnackbar

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp004toastsnackbar.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializace ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //nastaveni akce pro tlacitko Toast
        binding.btnShowToast.setOnClickListener {
            //nepouzivat velke T / to se pouyvia pro komponent
            val layoutInflater = LayoutInflater.from(this)
            val customToastLayout = layoutInflater.inflate(R.layout.custom_toast, null)

            val toastText = customToastLayout.findViewById<TextView>(R.id.toast_text)
            toastText.text = "Máš hlad?"
            //toastText.setTextColor(Color.BLACK)

            val toastIcon = customToastLayout.findViewById<ImageView>(R.id.toast_icon)
            toastIcon.setImageResource(R.drawable.ic_your_icon)
            //toastIcon.setBackgroundColor(Color.WHITE)

            customToastLayout.setPadding(8, 8, 8, 8)

            // ohraniceni
            val strokeDrawable = ShapeDrawable()
            strokeDrawable.paint.color = Color.BLACK // Barva ohraničení
            strokeDrawable.paint.strokeWidth = 2f // Tloušťka ohraničení
            strokeDrawable.paint.style = Paint.Style.STROKE

            customToastLayout.background = strokeDrawable

            val customToast = Toast(this)
            customToast.duration = Toast.LENGTH_SHORT
            customToast.view = customToastLayout
            customToast.show() }

            // akce pro tlacitko Zobrazit Snackbar

            binding.btnShowSnackBar.setOnClickListener {
                Snackbar.make(binding.root, "Já jsem SNACKBAR", Snackbar.LENGTH_SHORT)

                    .setDuration(7000)
                    .setBackgroundTint(Color.parseColor("#FF3578"))
                    .setTextColor(Color.BLACK)
                    .setActionTextColor(Color.WHITE)
                    .setAction("Zavřít"){
                        Toast.makeText(this, "Zaviram SNACKBAR", Toast.LENGTH_LONG).show()
                    }
                    .show()
            }
        }
    }