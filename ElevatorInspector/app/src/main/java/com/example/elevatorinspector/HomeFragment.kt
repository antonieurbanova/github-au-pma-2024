package com.example.elevatorinspector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavController
import com.example.elevatorinspector.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
    // Přístup k toolbaru
        val toolbar = requireActivity().findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)

        // Nastavení navigační ikony
        toolbar.setNavigationIcon(R.drawable.ic_menu)

        // Nastavení kliknutí na tlačítka a navigace
        binding.btnAddElevator.setOnClickListener {
            findNavController().navigate(R.id.action_to_addElevator)
        }

        binding.btnAddInspection.setOnClickListener {
            findNavController().navigate(R.id.action_to_addInspection)

        }

        binding.btnCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_to_calendar)
        }

        binding.btnElevatorList.setOnClickListener {
            findNavController().navigate(R.id.action_to_elevatorList)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
