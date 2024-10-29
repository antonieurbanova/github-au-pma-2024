package com.example.myapp003objednavka

import android.os.Bundle
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp003objednavka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Objednávka Cupcake"

        binding.btnObjednavka.setOnClickListener {
            val cupcakeRbId = binding.rgCupcakes.checkedRadioButtonId

            val cupcake = findViewById<RadioButton>(cupcakeRbId)

            val posypka = binding.cbPosypka.isChecked
            val jahoda = binding.cbJahoda.isChecked
            val kousky = binding.cbKousky.isChecked

            val objednavkaText = "Souhrn objednávky" +
                    "${cupcake.text}" +
                    (if(posypka) "; barevná posypka" else "") +
                    (if(jahoda) "; čerstvá jahoda" else "") +
                    (if(kousky) "; kousky čokolády" else "")

            binding.tvObjednavka.text = objednavkaText

            binding.rbChoco.setOnClickListener {
                binding.ivCupcake.setImageResource(R.drawable.choco)
            }
            binding.rbRed.setOnClickListener {
                binding.ivCupcake.setImageResource(R.drawable.red)
            }
            binding.rbVanilla.setOnClickListener {
                binding.ivCupcake.setImageResource(R.drawable.vanilla)
            }
        }

    }
}