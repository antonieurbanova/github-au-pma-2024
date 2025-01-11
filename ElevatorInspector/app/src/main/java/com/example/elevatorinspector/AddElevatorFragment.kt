package com.example.elevatorinspector

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.elevatorinspector.databinding.FragmentAddElevatorBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddElevatorFragment : Fragment() {

    private var _binding: FragmentAddElevatorBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    val vytahyRef = db.collection("vytahy")

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

        // Zkontrolování, zda jsou všechny hodnoty vyplněné
        if (nazev.isEmpty() || ulice.isEmpty() || cisloPopisne.isEmpty() || mesto.isEmpty() ||
            nosnost == null || patra == null) {
            Toast.makeText(requireContext(), "Vyplňte prosím všechny údaje.", Toast.LENGTH_SHORT).show()
            return
        }

        // Vytvoření objektu pro výtah
        val vytah = hashMapOf(
            "nazev" to nazev,
            "ulice" to ulice,
            "cisloPopisne" to cisloPopisne,
            "mesto" to mesto,
            "druh" to druh,
            "vaha" to nosnost,
            "patra" to patra
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
