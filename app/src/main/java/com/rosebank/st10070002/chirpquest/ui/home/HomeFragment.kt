package com.rosebank.st10070002.chirpquest.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.rosebank.st10070002.chirpquest.FlockFragment
import com.rosebank.st10070002.chirpquest.R
import com.rosebank.st10070002.chirpquest.ViewFindingsFragment
import com.rosebank.st10070002.chirpquest.databinding.FragmentHomeBinding
import com.rosebank.st10070002.chirpquest.ui.capture.CaptureFragment


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }
}