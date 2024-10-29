package com.example.myapp002myownlinearlayout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            //val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            //nsets

        val etName=findViewById<EditText>(R.id.etName)
        val etPlace=findViewById<EditText>(R.id.etPlace)
        val etDate=findViewById<EditText>(R.id.etDate)
        val etTime=findViewById<EditText>(R.id.etTime)
        val tvInvitation=findViewById<TextView>(R.id.tvInvitation)
        val btnSend=findViewById<Button>(R.id.btnSend)
        val btnDelete=findViewById<Button>(R.id.btnDelete)

        btnSend.setOnClickListener {
            val name = etName.text.toString()
            val place = etPlace.text.toString()
            val date = etDate.text.toString()
            val time = etTime.text.toString()

            val formattedtext = "Ahoj! Byl jsi pozván na akci $name. Koná se $date v čase $time na adrese $place! Přijď se podívat!"

            tvInvitation.text = formattedtext

        }

        btnDelete.setOnClickListener {
            etName.text.clear()
            etPlace.text.clear()
            etTime.text.clear()
            etDate.text.clear()

            tvInvitation.text = ""
        }
    }
}
