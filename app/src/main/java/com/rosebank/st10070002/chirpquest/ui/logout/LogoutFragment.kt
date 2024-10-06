package com.rosebank.st10070002.chirpquest.ui.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rosebank.st10070002.chirpquest.R

class LogoutFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        val root = inflater.inflate(R.layout.activity_login_page,container,false)

        return root

    }
}
