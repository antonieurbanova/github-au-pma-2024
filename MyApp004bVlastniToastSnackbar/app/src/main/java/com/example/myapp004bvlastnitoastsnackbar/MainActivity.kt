package com.example.myapp004bvlastnitoastsnackbar

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//změna
        
        val flavorSpinner = findViewById<Spinner>(R.id.flavorSpinner)
        val thinkCheckBox = findViewById<CheckBox>(R.id.thinkCheckBox)
        val orderButton = findViewById<Button>(R.id.orderButton)
        val cupcakeImageView = findViewById<ImageView>(R.id.cupcakeImageView)

        // Nastavení adapteru pro Spinner
        val flavors = arrayOf("Vanilka", "Čokoláda", "Red Velvet")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, flavors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        flavorSpinner.adapter = adapter

        // Akce po kliknutí na tlačítko "Objednat"
        orderButton.setOnClickListener {
            val selectedFlavor = flavorSpinner.selectedItem.toString()
            val isThinkChecked = thinkCheckBox.isChecked

            // Zobrazení obrázku cupcaku
            cupcakeImageView.isGone = false
            when (selectedFlavor) {
                "Vanilka" -> cupcakeImageView.setImageResource(R.drawable.cupcake_vanilla)
                "Čokoláda" -> cupcakeImageView.setImageResource(R.drawable.cupcake_choco)
                "Red Velvet" -> cupcakeImageView.setImageResource(R.drawable.cupcake_red)
            }

            // Pokud není zaškrtnutý CheckBox, zobrazí Toast
            if (!isThinkChecked) {
                Toast.makeText(this, "Objednáno!", Toast.LENGTH_SHORT).show()
            } else {
                // Pokud je zaškrtnutý CheckBox, zobrazí Snackbar
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Cupcake bude po chvilce objednán...",
                    Snackbar.LENGTH_LONG
                ).setAction("Vrátit") {
                    cupcakeImageView.isGone = true
                    Toast.makeText(this, "Dobře, zkus se podívat na ostatní příchutě!", Toast.LENGTH_LONG).show()
                }

                // Zobrazí Snackbar a po uplynutí času zobrazuje Toast
                snackbar.show()

                cupcakeImageView.postDelayed({
                    Toast.makeText(this, "Objednáno!", Toast.LENGTH_SHORT).show()
                }, 3000)  // Po 3 sekundách zobrazení Toastu
            }
        }
    }
}
