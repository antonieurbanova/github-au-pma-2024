package com.example.myapp004toastsnackbar

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
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
            //nepouzivat velke T / to se pouyvia pro komponentu
            val toast = Toast.makeText(this, "Nazdar - mam hlad", Toast.LENGTH_LONG)
            toast.show()
            
            // akce pro tlacitko Zobrazit Snackbar

            binding.btnShowSnackBar.setOnClickListener {
                Snackbar.make(binding.root, "Já jsem SNACKBAR", Snackbar.LENGTH_SHORT)

                    .setDuration(7000)
                    .setBackgroundTint(Color.parseColor("#FF3578"))
                    .setTextColor(Color.BLACK)
                    .setActionTextColor(Color.WHITE)
                    .setAction("Zavrit"){
                        Toast.makeText(this, "Zaviram SNACKBAR", Toast.LENGTH_LONG).show()
                    }
                    .show()
            }



            
        }
    }
}