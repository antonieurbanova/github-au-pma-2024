package com.example.vanocniaplikace

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("AdventCalendarPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val calendarLayout = findViewById<GridLayout>(R.id.calendar_layout)

        for (day in 1..24) {
            val dayView = FrameLayout(this).apply {
                setBackgroundColor(Color.RED)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 200
                    height = 200
                    setMargins(25, 8, 8, 8)
                }
            }

            val dayTextView = TextView(this).apply {
                text = day.toString()
                setTextColor(Color.WHITE)
                textSize = 18f
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }

            val chocolateImageView = ImageView(this).apply {
                setImageDrawable(null) // Prázdné, pokud není otevřeno
                visibility = View.GONE // Skryté čokoláda
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    setMargins(16, 16, 16, 16) // Trochu vnitřního odsazení pro obrázek
                }
            }

            val state = sharedPreferences.getString("day_$day", "closed")
            when (state) {
                "open" -> {
                    chocolateImageView.setImageResource(R.drawable.ic_chocolate)
                    chocolateImageView.visibility = View.VISIBLE
                    dayTextView.text = "" // Číslo zmizí
                }
                "eaten" -> {
                    chocolateImageView.setImageDrawable(null)
                    chocolateImageView.visibility = View.GONE
                    dayTextView.text = "" // Prázdné políčko
                }
            }

            dayView.setOnClickListener {
                val currentState = sharedPreferences.getString("day_$day", "closed")

                when (currentState) {
                    "closed" -> {
                        Snackbar.make(calendarLayout, "Bereš do ruky čokoládu z $day. dne.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Sníst!") {
                                Toast(this@MainActivity).apply {
                                    duration = Toast.LENGTH_SHORT
                                    view = layoutInflater.inflate(R.layout.toast_custom, null)
                                    show()
                                }
                                editor.putString("day_$day", "eaten").apply()
                                chocolateImageView.setImageDrawable(null)
                                chocolateImageView.visibility = View.GONE
                                dayTextView.text = "" // Prázdné políčko po snědení
                            }.show()

                        editor.putString("day_$day", "open").apply()
                        dayTextView.text = "" // Číslo zmizí po otevření
                        chocolateImageView.setImageResource(R.drawable.ic_chocolate)
                        chocolateImageView.visibility = View.VISIBLE
                    }
                    "open" -> {
                        Snackbar.make(calendarLayout, "Bereš do ruky čokoládu z $day. dne.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Sníst!") {
                                Toast(this@MainActivity).apply {
                                    duration = Toast.LENGTH_SHORT
                                    view = layoutInflater.inflate(R.layout.toast_custom, null)
                                    show()
                                }
                                editor.putString("day_$day", "eaten").apply()
                                chocolateImageView.setImageDrawable(null)
                                chocolateImageView.visibility = View.GONE
                                dayTextView.text = "" // Prázdné políčko po snědení
                            }.show()
                    }
                }
            }

            dayView.addView(chocolateImageView) // Přidání čokolády
            dayView.addView(dayTextView)       // Přidání čísla dne
            calendarLayout.addView(dayView)    // Přidání políčka do GridLayout
        }
    }
}
