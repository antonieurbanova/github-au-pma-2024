package com.example.elevatorinspector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.elevatorinspector.databinding.FragmentAddInspectionBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddInspectionFragment : Fragment() {

    private var _binding: FragmentAddInspectionBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val vytahyRef = db.collection("vytahy")

    private var elevatorNames = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddInspectionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Načtení výtahů z Firestore
        fetchElevators()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
