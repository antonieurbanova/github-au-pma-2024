package com.example.myapp014mynotehub

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp014mynotehub.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

private lateinit var binding: ActivityMainBinding
private lateinit var noteAdapter: NoteAdapter
private lateinit var database: NoteHubDatabase

// Přidání proměnných pro filtrování a řazení
private var isNameAscending = true // Pro sledování stavu řazení podle názvu
private var currentCategory: String = "Vše" // Aktuálně vybraná kategorie


override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Inicializace databáze
    database = NoteHubDatabaseInstance.getDatabase(this)

    // Vložení výchozích kategorií a štítků do databáze
    insertDefaultCategories()
    //insertDefaultTags()

    // Inicializace RecyclerView
    binding.recyclerView.layoutManager = LinearLayoutManager(this)
    //noteAdapter = NoteAdapter(getSampleNotes())

    //noteAdapter = NoteAdapter(emptyList()) // Inicializace s prázdným seznamem
    //binding.recyclerView.adapter = noteAdapter

    // Vložení testovacích dat
    //insertSampleNotes()

    // Načtení poznámek z databáze
    loadNotes()

    binding.fabAddNote.setOnClickListener {
        showAddNoteDialog()
    }
    // Nastavení uživatelského rozhraní (filtry, řazení atd.)
    setupUI()
}


private fun addNoteToDatabase(title: String, content: String, categoryId: Int) {
    lifecycleScope.launch {
        val newNote = Note(title = title, content = content, categoryId = categoryId)
        database.noteDao().insert(newNote)  // Vloží poznámku do databáze
        loadNotes()  // Aktualizuje seznam poznámek
    }
}
private fun showAddNoteDialog() {
    val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
    val titleEditText = dialogView.findViewById<EditText>(R.id.editTextTitle)
    val contentEditText = dialogView.findViewById<EditText>(R.id.editTextContent)
    val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)

    // Načtení kategorií z databáze a jejich zobrazení ve Spinneru
    lifecycleScope.launch {
        val categories = database.categoryDao().getAllCategories().first()  // Načteme kategorie
        val categoryNames = categories.map { it.name }  // Převedeme na seznam názvů kategorií
        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    val dialog = AlertDialog.Builder(this)
        .setTitle("Přidat poznámku")
        .setView(dialogView)
        .setPositiveButton("Přidat") { _, _ ->
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            val selectedCategory = spinnerCategory.selectedItem.toString()  // Získáme vybranou kategorii

            // Najdeme ID vybrané kategorie
            lifecycleScope.launch {
                val category = database.categoryDao().getCategoryByName(selectedCategory)
                if (category != null) {
                    addNoteToDatabase(title, content, category.id)
                }
            }
        }
        .setNegativeButton("Zrušit", null)
        .create()

    dialog.show()
}

private fun loadNotes() {
    lifecycleScope.launch {
        var notes = if (currentCategory == "Vše") {
            database.noteDao().getAllNotes().first()
        } else {
            val category = database.categoryDao().getCategoryByName(currentCategory)
            if (category != null) {
                database.noteDao().getNotesByCategoryId(category.id).first()
            } else {
                emptyList()
            }
        }

        // Aplikujeme řazení podle názvu
        if (isNameAscending) {
            notes = notes.sortedWith(compareBy { it.title?.lowercase() ?: "" }) // Ignorujeme velká/malá písmena
        } else {
            notes = notes.sortedWith(compareByDescending { it.title?.lowercase() ?: "" })
        }

        // Aktualizace RecyclerView
        noteAdapter = NoteAdapter(
            notes = notes,
            onDeleteClick = { note -> deleteNote(note) },
            onEditClick = { note -> editNote(note) },
            lifecycleScope = lifecycleScope,
            database = database
        )
        binding.recyclerView.adapter = noteAdapter
    }
}

private fun clearDatabase() {
    lifecycleScope.launch {
        // Smazání všech poznámek
        database.noteDao().deleteAllNotes()

        // Smazání všech kategorií
        database.categoryDao().deleteAllCategories()

        // Resetování auto-increment hodnoty
        resetAutoIncrement("note_table")
        resetAutoIncrement("category_table")
    }
    // Pro účely ladění: odkomentujeme jen v případě, že chceme  vymazat tabulky a resetovat ID
    // clearDatabase()
}

private fun resetAutoIncrement(tableName: String) {
    lifecycleScope.launch {
        database.openHelper.writableDatabase.execSQL("DELETE FROM sqlite_sequence WHERE name = '$tableName'")
    }
}


private fun insertSampleNotes() {
    lifecycleScope.launch {
        val sampleNotes = listOf(
            Note(title = "Vzorek 1", content = "Obsah první testovací poznámky"),
            Note(title = "Vzorek 2", content = "Obsah druhé testovací poznámky"),
            Note(title = "Vzorek 3", content = "Obsah třetí testovací poznámky")
        )
        sampleNotes.forEach { database.noteDao().insert(it) }
    }
}
private fun deleteNote(note: Note) {
    lifecycleScope.launch {
        database.noteDao().delete(note)  // Smazání poznámky z databáze
        loadNotes()  // Aktualizace seznamu poznámek
    }
}
private fun editNote(note: Note) {
    val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
    val titleEditText = dialogView.findViewById<EditText>(R.id.editTextTitle)
    val contentEditText = dialogView.findViewById<EditText>(R.id.editTextContent)
    val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)


    // Předvyplnění stávajících dat poznámky
    titleEditText.setText(note.title)
    contentEditText.setText(note.content)

    // Načtení kategorií z databáze a přednastavení aktuální kategorie
    lifecycleScope.launch {
        val categories = database.categoryDao().getAllCategories().first()
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Nastavení aktuální kategorie poznámky jako výchozí hodnoty Spinneru
        val currentCategory = categories.find { it.id == note.categoryId }
        val currentCategoryIndex = categoryNames.indexOf(currentCategory?.name)
        if (currentCategoryIndex >= 0) {
            spinnerCategory.setSelection(currentCategoryIndex)
        }
    }

    val dialog = AlertDialog.Builder(this)
        .setTitle("Upravit poznámku")
        .setView(dialogView)
        .setPositiveButton("Uložit") { _, _ ->
            val updatedTitle = titleEditText.text.toString()
            val updatedContent = contentEditText.text.toString()
            val selectedCategoryName = spinnerCategory.selectedItem.toString()

            // Najdeme ID vybrané kategorie
            lifecycleScope.launch {
                val selectedCategory = database.categoryDao().getCategoryByName(selectedCategoryName)
                if (selectedCategory != null) {
                    val updatedNote = note.copy(
                        title = updatedTitle,
                        content = updatedContent,
                        categoryId = selectedCategory.id
                    )
                    database.noteDao().update(updatedNote) // Aktualizace poznámky v databázi
                    loadNotes() // Aktualizace seznamu poznámek
                }
            }
        }
        .setNegativeButton("Zrušit", null)
        .create()

    dialog.show()
}

private fun insertDefaultCategories() {
    lifecycleScope.launch {
        val defaultCategories = listOf(
            "Osobní",
            "Práce",
            "Nápady"
        )

        for (categoryName in defaultCategories) {
            val existingCategory = database.categoryDao().getCategoryByName(categoryName)
            if (existingCategory == null) {
                // Kategorie s tímto názvem neexistuje, vložíme ji
                database.categoryDao().insert(Category(name = categoryName))
            }
        }
    }
}
private fun setupUI() {
    setupFilterSpinner()
    setupSortButtons()
}
//Spinner pro výběr kategorie:
//Stejně jako u přidávání poznámky, načítáme kategorie z databáze.
//Nastavíme předvybranou kategorii podle aktuální poznámky.
private fun setupFilterSpinner() {
    lifecycleScope.launch {
        val categories = database.categoryDao().getAllCategories().first()
        val categoryNames = categories.map { it.name }.toMutableList()
        categoryNames.add(0, "Vše") // Přidáme možnost "Vše"

        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterCategory.adapter = adapter

        // Nastavení OnItemSelectedListener
        binding.spinnerFilterCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCategory = categoryNames[position]
                loadNotes() // Načte poznámky podle vybrané kategorie
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Není třeba nic dělat, když není nic vybráno
            }
        }
    }
}

private fun setupSortButtons() {
    binding.btnSortByName.setOnClickListener {
        isNameAscending = !isNameAscending
        loadNotes()
    }
}


/*private fun getSampleNotes(): List<Note> {
    // Testovací seznam poznámek
    return listOf(
        Note(title = "Poznámka 1", content = "Obsah první poznámky"),
        Note(title = "Poznámka 2", content = "Obsah druhé poznámky"),
        Note(title = "Poznámka 3", content = "Obsah třetí poznámky")
    )
}*/


}