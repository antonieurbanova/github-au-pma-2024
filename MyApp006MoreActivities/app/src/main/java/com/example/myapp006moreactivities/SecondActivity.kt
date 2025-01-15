package com.example.myapp006moreactivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp006moreactivities.databinding.ActivityMainBinding
import org.w3c.dom.Text
import com.example.myapp006moreactivities.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        binding = ActivitySecondBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //setContentView(R.layout.activity_second)

        val twInfo = binding.twInfo

        // Nacteni dat z intentu
        val nickname = intent.getStringExtra("NICK_NAME")
        twInfo.text = "Jmeno z prvni aktivity: $nickname"


        val btnThird = binding.btnThird

        btnThird.setOnClickListener {
            val intent = Intent(this,ThirdActivity::class.java)
            startActivity(intent)
        }

        val btnBack = binding.btnBack
        btnBack.setOnClickListener {
            finish()
        }

    }
}
