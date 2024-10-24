package com.example.myapp007fragments

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.myapp007fragments.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
       // enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFragment1.setOnClickListener {
            replaceFragment(Fragment1())
        }

        binding.btnFragment2.setOnClickListener {
            replaceFragment(Fragment2())
        }

       // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
         //   val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
           // v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            //insets
        }

    private fun replaceFragment(fragment : Fragment) {
        // ziska instanci spravce fragmentu
        val fragmentManager = supportFragmentManager

        // vytvori novou transakci s fragmenty
        val fragmentTransaction = fragmentManager.beginTransaction()

        // nahradi fragment v kontejneru novym fragmentem, ktery byl predan jako argument
        fragmentTransaction.replace(R.id.FragmentContainer, fragment)

        // povtrdi transakci a provede vymenu fragmentu
        fragmentTransaction.commit()

    }

}


