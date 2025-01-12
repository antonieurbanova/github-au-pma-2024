package com.example.elevatorinspector

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.elevatorinspector.databinding.FragmentAddElevatorBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddElevatorFragment : Fragment() {

    private var _binding: FragmentAddElevatorBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    val vytahyRef = db.collection("vytahy")


    //kalendar
    private fun setupDatePicker() {
        val etPrvniOZOP = binding.etPrvniOZOP

        etPrvniOZOP.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Zobrazení DatePickerDialog
            DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Nastavení vybraného data do EditText
                    val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    etPrvniOZOP.setText(formattedDate)
                },
                year,
                month,
                day
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddElevatorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Nastavení tlačítka pro přidání výtahu
        binding.btnSaveElevator.setOnClickListener {
            addElevatorToFirestore()
        }

        // kalendar
        setupDatePicker()

        return root
    }

    private fun addElevatorToFirestore() {
        // Získání dat z formuláře
        val nazev = binding.etNazev.text.toString()
        val ulice = binding.etUlice.text.toString()
        val cisloPopisne = binding.etCisloPopisne.text.toString()
        val mesto = binding.etMesto.text.toString()
        val druh = binding.spinnerDruh.selectedItem.toString()
        val nosnost = binding.etNosnost.text.toString().toIntOrNull()
        val patra = binding.etPatra.text.toString().toIntOrNull()
        val prvniOZOP = binding.etPrvniOZOP.text.toString()

        // Zkontrolování, zda jsou všechny hodnoty vyplněné
        if (nazev.isEmpty() || ulice.isEmpty() || cisloPopisne.isEmpty() || mesto.isEmpty() ||
            nosnost == null || patra == null || prvniOZOP.isEmpty()) {
            Toast.makeText(requireContext(), "Vyplňte prosím všechny údaje.", Toast.LENGTH_SHORT).show()
            return
        }

        //Převedení data první prohlidky na timestamp
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = simpleDateFormat.parse(prvniOZOP)
        val timestamp = Timestamp(date)


        // Vytvoření hodnot pro plannedOZ a plannedOP podle typu výtahu
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Podmínky pro osobní a jídelní výtah
        val plannedOZ: Timestamp
        val plannedOP: Timestamp

        if (druh == "Osobní") {
            // Osobní výtah
            calendar.add(Calendar.MONTH, 3) // OP za 3 měsíce
            plannedOP = Timestamp(calendar.time)

            calendar.add(Calendar.YEAR, 1) // OZ za 1 rok
            plannedOZ = Timestamp(calendar.time)
        } else if (druh == "Jídelní") {
            // Jídelní výtah
            calendar.add(Calendar.MONTH, 6) // OP za 6 měsíců
            plannedOP = Timestamp(calendar.time)

            calendar.add(Calendar.YEAR, 3) // OZ za 3 roky
            plannedOZ = Timestamp(calendar.time)
        } else {
            // Pokud je zadán neznámý typ výtahu, použijeme defaultní hodnoty - musí zde aby byly hodnoty inicializovány
            plannedOP = Timestamp(calendar.time)
            plannedOZ = Timestamp(calendar.time)
        }


        // Vytvoření objektu pro výtah
        val vytah = hashMapOf(
            "nazev" to nazev,
            "ulice" to ulice,
            "cisloPopisne" to cisloPopisne,
            "mesto" to mesto,
            "druh" to druh,
            "vaha" to nosnost,
            "patra" to patra,
            "prvniOZOP" to timestamp,
            "plannedOZ" to plannedOZ,
            "plannedOP" to plannedOP
        )

        //Log.d("AddElevatorFragment", "Elevator data: $vytah")

        // Uložení výtahu do Firestore
       vytahyRef
            .add(vytah)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireContext(), "Výtah byl úspěšně přidán!", Toast.LENGTH_SHORT).show()
                // Přesměrování na jiný fragment po úspěšném přidání
                findNavController().navigate(R.id.homeFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Chyba při přidávání výtahu: ${e.message}", Toast.LENGTH_SHORT).show()
                //Log.e("AddElevatorFragment", "Error adding elevator: ${e.message}")
                //findNavController().navigate(R.id.homeFragment)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
