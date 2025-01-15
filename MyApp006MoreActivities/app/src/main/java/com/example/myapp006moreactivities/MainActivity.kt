package com.example.myapp006moreactivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myapp006moreactivities.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the Toolbar as the ActionBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val btnSecond = binding.btnSecond
        val etNickname = binding.etNickname

        btnSecond.setOnClickListener {
            val nickname = etNickname.text.toString() //získáme text z edit text pole
            val intent = Intent(this,SecondActivity::class.java)
            intent.putExtra("NICK_NAME", nickname)
            startActivity(intent)
        }


    }
}



