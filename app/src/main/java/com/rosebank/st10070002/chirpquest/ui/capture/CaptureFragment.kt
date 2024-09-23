package com.rosebank.st10070002.chirpquest.ui.capture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rosebank.st10070002.chirpquest.R
import com.rosebank.st10070002.chirpquest.databinding.FragmentHomeBinding

class CaptureFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_capture,container,false)

        return root
    }
}