package com.example.myapp010datastore

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

import android.widget.*
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class MainActivity : AppCompatActivity() {
    //definice klicu pro ukladani dat do datastore
    private val NAME_KEY = stringPreferencesKey("name")
    private val AGE_KEY = intPreferencesKey("age")
    private val CHECKBOX_KEY = booleanPreferencesKey("checkbox")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nameEditText = findViewById<EditText>(R.id.etName)
        val ageEditText = findViewById<EditText>(R.id.etAge)
        val checkBox = findViewById<CheckBox>(R.id.cbAdult)
        val saveButton = findViewById<Button>(R.id.btnSave)
        val loadButton = findViewById<Button>(R.id.btnLoad)

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val ageString = ageEditText.text.toString()
            val isChecked = checkBox.isChecked

            if (name.isBlank()) {
                Toast.makeText(this, "Hele, vyplň jméno...", Toast.LENGTH_SHORT).show()
            } else if (ageString.isBlank()) {
                Toast.makeText(this, "Hele, vyplň věk...", Toast.LENGTH_SHORT).show()
            } else {
                val age = ageString.toIntOrNull()
                if (age == null) {
                    Toast.makeText(this, "Zadej platný věk...", Toast.LENGTH_SHORT).show()
                } else {
                    // Logika pro kontrolu věku a checkboxu
                    val isAdult = age >= 18
                    if ((age < 18 && isChecked) || (age >= 18 && !isChecked)) {
                        Toast.makeText(this, "Kecáš, takže nic ukládat nebudu", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "Jasně, ukládám...", Toast.LENGTH_SHORT).show()

                        // Uložení dat pomocí DataStore
                        CoroutineScope(Dispatchers.IO).launch {
                            saveData(name, age, isChecked)
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Data uložena!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val age = ageEditText.text.toString().toIntOrNull() ?: 0
            val isChecked = checkBox.isChecked

            CoroutineScope(Dispatchers.IO).launch {
                saveData(name, age, isChecked)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Data uložena!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loadButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val data = loadData()
                runOnUiThread {
                    nameEditText.setText(data.first)
                    ageEditText.setText(data.second.toString())
                    checkBox.isChecked = data.third
                    Toast.makeText(this@MainActivity, "Data načtena!", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private suspend fun saveData(name: String, age: Int, isChecked: Boolean) {
        dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
            prefs[AGE_KEY] = age
            prefs[CHECKBOX_KEY] = isChecked
        }
    }

    private suspend fun loadData(): Triple<String, Int, Boolean> {
        val prefs = dataStore.data.first()
        val name = prefs[NAME_KEY] ?: ""
        val age = prefs[AGE_KEY] ?: 0
        val isChecked = prefs[CHECKBOX_KEY] ?: false
        return Triple(name, age, isChecked)
    }
}