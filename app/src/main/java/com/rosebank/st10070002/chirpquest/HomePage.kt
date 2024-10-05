package com.rosebank.st10070002.chirpquest

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.rosebank.st10070002.chirpquest.ui.capture.CaptureFragment
import com.rosebank.st10070002.chirpquest.ui.flock.FlockFragment
import com.rosebank.st10070002.chirpquest.ui.FindingsListing.FindingsListingFragment
// import com.rosebank.st10070002.chirpquest.ui.nearby.NearbyFragment

class HomePage : AppCompatActivity() {

/*
    private fun replaceFragment(Fragment: fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager()
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_content_main, fragment)
        fragmentTransaction.commit()
    }

 */

    fun nearbyButtonClick(view: View) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, NearbyFragment())
            .commit()
    }

    fun captureButtonClick(view: View) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, CaptureFragment())
            .commit()
    }

    fun findingsButtonClick(view: View) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, ViewFindingsFragment())
            .commit()
    }

    fun flockButtonClick(view: View) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, FlockFragment())
            .commit()
    }
}