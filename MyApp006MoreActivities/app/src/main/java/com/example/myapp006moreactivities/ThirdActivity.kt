package com.example.myapp006moreactivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp006moreactivities.databinding.ActivityThirdBinding

class ThirdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThirdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        binding = ActivityThirdBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //setContentView(R.layout.activity_second)

        val twInfo = binding.twInfo

        val btnBack = binding.btnBack
        btnBack.setOnClickListener {
            finish()
        }

    }
}