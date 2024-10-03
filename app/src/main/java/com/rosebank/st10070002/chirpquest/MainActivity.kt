package com.rosebank.st10070002.chirpquest

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.rosebank.st10070002.chirpquest.databinding.ActivityMainBinding
import com.rosebank.st10070002.chirpquest.ui.capture.CaptureFragment
import com.rosebank.st10070002.chirpquest.ui.home.HomeFragment

import com.rosebank.st10070002.chirpquest.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        // Get the username passed from LoginActivity
        val username = intent.getStringExtra("username")

        // Create the SettingsFragment and pass the username as an argument
        val fragment = SettingsFragment()
        val bundle = Bundle()
        bundle.putString("username", username)
        fragment.arguments = bundle

        // Floating action button setup
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Set up the ActionBarDrawerToggle (burger menu)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_nearby, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_findings_listing, R.id.nav_flock, R.id.nav_ViewFindingsFragment, R.id.nav_capture, R.id.nav_settings
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Set up NavigationView item selection handling
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    // Replace fragment with HomeFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, HomeFragment())
                        .commit()
                }
                R.id.menu_findings -> {
                    // Replace fragment with CreateFindingsFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, ViewFindingsFragment())
                        .commit()
                }
                R.id.menu_nearby -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, NearbyFragment())
                        .commit()
                }
                R.id.menu_capture -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, CaptureFragment())
                        .commit()
                }
                R.id.menu_settings -> {
                    // Replace fragment with SettingsFragment and pass the username
                    val settingsFragment = SettingsFragment()
                    val bundle = Bundle().apply {
                        putString("username", username) // Pass the username here
                    }
                    settingsFragment.arguments = bundle

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, settingsFragment)
                        .commit()
                }
                // Handle other menu items
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Load the default fragment (HomeFragment) when the app starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, HomeFragment())
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
