package com.example.elevatorinspector

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.elevatorinspector.databinding.FragmentCalendarBinding
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

        // Nastavit posluchač pro kliknutí na den
        calendarView.setOnDateChangedListener { _, date, _ ->
            onDateSelected(date)
        }

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
        val plannedColor = ContextCompat.getColor(requireContext(), R.color.yellow)
        calendarView.addDecorator(EventDecorator(plannedColor, plannedDates))
    }

    private fun highlightInspectionDates(
        snapshot: QuerySnapshot,
        calendarView: MaterialCalendarView,
        color: Int
    ) {

        val vcasDates = mutableSetOf<CalendarDay>()
        val pozdeDates = mutableSetOf<CalendarDay>()

        for (doc in snapshot.documents) {
            val date = doc.getTimestamp("datum")?.toDate()
            val vcas = doc.getBoolean("vcas") ?: true

            date?.let {
                val calendarDay = dateToCalendarDay(it)
                if (vcas) {
                    vcasDates.add(calendarDay)
                } else {
                    pozdeDates.add(calendarDay)
                }
            }
        }

        val vcasColor = ContextCompat.getColor(requireContext(), R.color.green)
        val pozdeColor = ContextCompat.getColor(requireContext(), R.color.red)

        calendarView.addDecorator(EventDecorator(vcasColor, vcasDates))
        calendarView.addDecorator(EventDecorator(pozdeColor, pozdeDates))

    }


    // Pomocná funkce pro převod Date na CalendarDay

    private fun dateToCalendarDay(date: Date): CalendarDay {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return CalendarDay.from(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun dateToDate(calendarDay: CalendarDay): Date {
        val calendar = Calendar.getInstance()
        calendar.set(calendarDay.year, calendarDay.month - 1, calendarDay.day)
        return calendar.time
    }

    private fun onDateSelected(date: CalendarDay) {
        val selectedDate = dateToDate(date)
        //Log.d("CalendarFragment", "Vybraný datum: $selectedDate")  // Logování vybraného data

        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val startOfDay = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = calendar.time

        //Log.d("CalendarFragment", "Načítání z Firebase pro datum: $startOfDay až $endOfDay")  // Logování doby načítání

        db.collection("vytahy")
            .whereGreaterThanOrEqualTo("plannedOP", startOfDay)
            .whereLessThanOrEqualTo("plannedOP", endOfDay)
            .get()
            .addOnSuccessListener { opSnapshot ->
                val records = mutableListOf<String>()

                for (doc in opSnapshot.documents) {
                    records.add(
                        "Poslední den pro provedení včasné OP pro ${
                            doc.getString("druh")?.lowercase()
                        } výtah ${doc.getString("nazev")} na adrese ${doc.getString("ulice")} ${
                            doc.getString(
                                "cisloPopisne"
                            )
                        }, ${doc.getString("mesto")}!"
                    )
                }

                db.collection("vytahy")
                    .whereGreaterThanOrEqualTo("plannedOZ", startOfDay)
                    .whereLessThanOrEqualTo("plannedOZ", endOfDay)
                    .get()
                    .addOnSuccessListener { ozSnapshot ->

                        for (doc in ozSnapshot.documents) {
                            records.add(
                                "Měla by být provedena OZ pro ${
                                    doc.getString("druh")?.lowercase()
                                } výtah ${doc.getString("nazev")} na adrese ${doc.getString("ulice")} ${
                                    doc.getString(
                                        "cisloPopisne"
                                    )
                                }, ${doc.getString("mesto")}."
                            )
                        }


                        db.collection("prohlidky")
                            .whereGreaterThanOrEqualTo("datum", startOfDay)
                            .whereLessThanOrEqualTo("datum", endOfDay)
                            .get()
                            .addOnSuccessListener { inspectionsSnapshot ->
                                //Log.d("CalendarFragment", "Dostala jsem se do dovnitř: $startOfDay až $endOfDay")  // Logování doby načítání

                                // Loguj počet dokumentů, které byly vráceny dotazem
                                //Log.d("CalendarFragment", "Počet prohlídek: ${inspectionsSnapshot.size()}")


                                for (doc in inspectionsSnapshot.documents) {
                                    val vcas = doc.getBoolean("vcas") ?: false
                                    val status = if (vcas) "včas" else "pozdě"

                                        records.add(
                                            "${doc.getString("jmenoInspektora")} provedl/a ${
                                                doc.getString(
                                                    "prohlidkaTyp"
                                                )
                                            } pro výtah s názvem ${doc.getString("vytah")}. ${
                                                doc.getString(
                                                    "prohlidkaTyp"
                                                )
                                            } byla provedena $status."
                                        )
                                    }

                                if (records.isEmpty()) {
                                    records.add("Žádné záznamy pro tento den.")
                                }

                                // Log.d("CalendarFragment", "Záznamy pro tento den: $records")  // Logování záznamů

                                showRecordsDialog(records, selectedDate)
                            }
                    }
            }
        }

    private fun showRecordsDialog(records: List<String>, date: Date) {
        // Formátování data na "den/měsíc/rok"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        val message = if (records.isNotEmpty()) {
            records.joinToString("\n")
        } else {
            "Pro tento den nebyly nalezeny žádné záznamy."
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Záznamy pro ${formattedDate}")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }


    // Třída dekorátoru
    class EventDecorator(
        private val color: Int,
        private val dates: Set<CalendarDay>
    ) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(8f, color)) // Přidání tečky s příslušnou barvou
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

