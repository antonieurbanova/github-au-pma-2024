package com.example.elevatorinspector

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.elevatorinspector.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.Calendar
import java.util.Date

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val calendarView: MaterialCalendarView = binding.calendarView

        // Načti data z Firebase a aplikuj dekorátory
        fetchFirebaseData(calendarView)

        // Zde můžete přidat logiku pro zvýraznění dnů, přidání dekorátorů atd.

        return root
    }

    private val db = FirebaseFirestore.getInstance()

    private fun fetchFirebaseData(calendarView: MaterialCalendarView) {
        // Načti data z kolekce "vytahy"
        db.collection("vytahy").get().addOnSuccessListener { snapshot ->
            highlightPlannedDates(snapshot, calendarView, Color.BLUE)
        }

        // Načti data z kolekce "prohlidky"
        db.collection("prohlidky").get().addOnSuccessListener { snapshot ->
            highlightInspectionDates(snapshot, calendarView, Color.GREEN)
        }
    }

    private fun highlightPlannedDates(
        snapshot: QuerySnapshot,
        calendarView: MaterialCalendarView,
        color: Int
    ) {
        val plannedDates = mutableSetOf<CalendarDay>()
        for (doc in snapshot.documents) {
            val plannedOP = doc.getTimestamp("plannedOP")?.toDate()
            val plannedOZ = doc.getTimestamp("plannedOZ")?.toDate()

            plannedOP?.let { plannedDates.add(dateToCalendarDay(it)) }
            plannedOZ?.let { plannedDates.add(dateToCalendarDay(it)) }
        }

        calendarView.addDecorator(EventDecorator(color, plannedDates))
    }

    private fun highlightInspectionDates(
        snapshot: QuerySnapshot,
        calendarView: MaterialCalendarView,
        color: Int
    ) {
        val inspectionDates = mutableSetOf<CalendarDay>()
        for (doc in snapshot.documents) {
            val date = doc.getTimestamp("datum")?.toDate()
            date?.let { inspectionDates.add(dateToCalendarDay(it)) }
        }

        calendarView.addDecorator(EventDecorator(color, inspectionDates))
    }

    private fun dateToCalendarDay(date: Date): CalendarDay {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return CalendarDay.from(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // Dekorátor pro zvýraznění dnů
    class EventDecorator(private val color: Int, private val dates: Set<CalendarDay>) :
        DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(8f, color)) // Přidání barevného tečkovaného zvýraznění
        }
    }
}