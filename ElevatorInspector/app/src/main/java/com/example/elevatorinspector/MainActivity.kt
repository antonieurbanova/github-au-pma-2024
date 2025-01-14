package com.example.elevatorinspector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Nastavení NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Nastavení MaterialToolBar
        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // Spojení toolbaru s NavControllerem
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Listener pro změnu navigační ikony
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.homeFragment) {
                // Nastavení vlastní ikony pro fragmenty mimo HomeFragment
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_back)
            } else {
                // Skrytí ikony pro HomeFragment
                toolbar.navigationIcon = null
            }
        }

        // Inicializace Firebase
       // FirebaseApp.initializeApp(this)
       // println("Firebase initialized successfully")

    }

    //Akce tlačítka zpět
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
