package com.example.elevatorinspector

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.elevatorinspector.databinding.FragmentAddInspectionBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddInspectionFragment : Fragment() {

    private var _binding: FragmentAddInspectionBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val vytahyRef = db.collection("vytahy")
    private val prohlidkyRef = db.collection("prohlidky")

    private var elevatorNames = mutableListOf<String>()

    private fun setupDatePicker() {
        val etDatumProvedeni = binding.etDatumProvedeni

        etDatumProvedeni.setOnClickListener {
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
                    etDatumProvedeni.setText(formattedDate)
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
        _binding = FragmentAddInspectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Načtení výtahů z Firestore
        fetchElevators()

        // Nastavení tlačítka pro přidání prohlídky
        binding.btnSaveInspection.setOnClickListener {
            addInspectionToFirestore()
        }

        // Nastavení DatePickeru
        setupDatePicker()

        return root
    }

    private fun fetchElevators() {
        vytahyRef.get()
            .addOnSuccessListener { result ->
                elevatorNames.clear()
                for (document in result) {
                    val elevatorName = document.getString("nazev") ?: "Neznámý"
                    elevatorNames.add(elevatorName)
                }
                setupElevatorSpinner()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Chyba při načítání výtahů: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupElevatorSpinner() {
        // Vytvoření ArrayAdapteru pro Spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            elevatorNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Nastavení adapteru pro Spinner
        binding.spinnerVytah.adapter = adapter
    }

    private fun addInspectionToFirestore() {
        // Získání dat z formuláře
        val vytah = binding.spinnerVytah.selectedItem.toString()
        val prohlidkaTyp = binding.spinnerProhlidka.selectedItem.toString()
        val datumProvedeni = binding.etDatumProvedeni.text.toString()
        val jmenoInspektora = binding.etJmenoInspektora.text.toString()

        // Kontrola, zda jsou všechna pole vyplněná
        if (vytah.isEmpty() || prohlidkaTyp.isEmpty() || datumProvedeni.isEmpty() || jmenoInspektora.isEmpty()) {
            Toast.makeText(requireContext(), "Vyplňte prosím všechny údaje.", Toast.LENGTH_SHORT).show()
            return
        }

        //Převedení data prohlidky na timestamp
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = simpleDateFormat.parse(datumProvedeni)
        val timestamp = Timestamp(date)

        // Získání výtahu z databáze podle jména (spojení s výtahem pro aktualizaci plánovaných prohlídek)
        val elevatorRef = vytahyRef.whereEqualTo("nazev", vytah).limit(1)
        elevatorRef.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "Výtah nebyl nalezen.", Toast.LENGTH_SHORT)
                        .show()
                    return@addOnSuccessListener
                }

                val elevator = documents.first()
                val plannedOZ = elevator.getTimestamp("plannedOZ") ?: Timestamp.now()
                val plannedOP = elevator.getTimestamp("plannedOP") ?: Timestamp.now()

                val calendar = Calendar.getInstance()
                calendar.time = date

                // Aktualizace dat podle typu prohlídky
                val newPlannedOZ: Timestamp
                val newPlannedOP: Timestamp
                val vcas: Boolean

                if (prohlidkaTyp == "OZ") {
                    if (vytah == "Osobní") {
                        calendar.add(Calendar.YEAR, 1)
                        newPlannedOZ = Timestamp(calendar.time)
                    } else { // Jídelní výtah
                        calendar.add(Calendar.YEAR, 3)
                        newPlannedOZ = Timestamp(calendar.time)
                    }
                    newPlannedOP = plannedOP // OP zůstává nezměněné
                    vcas = timestamp.toDate().compareTo(plannedOP.toDate()) <= 0
                } else { // OP
                    if (vytah == "Osobní") {
                        calendar.add(Calendar.MONTH, 3)
                        newPlannedOP = Timestamp(calendar.time)
                    } else { // Jídelní výtah
                        calendar.add(Calendar.MONTH, 6)
                        newPlannedOP = Timestamp(calendar.time)
                    }
                    newPlannedOZ = plannedOZ // OZ zůstává nezměněné
                    vcas = timestamp.toDate().compareTo(newPlannedOP.toDate()) <= 0
                }

                // Vytvoření objektu pro prohlídku
                val prohlidka = hashMapOf(
                    "vytah" to vytah,
                    "prohlidkaTyp" to prohlidkaTyp,
                    "datum" to timestamp,
                    "jmenoInspektora" to jmenoInspektora,
                    "vcas" to vcas
                )


                // Uložení prohlídky do Firestore
                prohlidkyRef
                    .add(prohlidka)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            requireContext(),
                            "Prohlídka byla úspěšně přidána!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Aktualizace plánovaných prohlídek pro výtah
                        val elevatorUpdates: MutableMap<String, Any> = hashMapOf(
                            "plannedOZ" to newPlannedOZ,
                            "plannedOP" to newPlannedOP
                        )
                        elevator.reference.update(elevatorUpdates)

                        // Přesměrování na jiný fragment po úspěšném přidání
                        findNavController().navigate(R.id.homeFragment)
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Chyba při přidávání prohlídky: ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
