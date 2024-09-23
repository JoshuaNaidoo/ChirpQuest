package com.rosebank.st10070002.chirpquest.ui.neaarby

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rosebank.st10070002.chirpquest.R
import com.rosebank.st10070002.chirpquest.R.layout
import com.rosebank.st10070002.chirpquest.databinding.FragmentHomeBinding
import com.rosebank.st10070002.chirpquest.ui.home.HomeViewModel


class NearbyFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_nearby,container,false)

        return root
    }
}
