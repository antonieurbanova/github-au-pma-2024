package com.example.myapp004toastsnackbar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp004toastsnackbar.databinding.ActivityMainBinding

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

            
            
        }
    }
}