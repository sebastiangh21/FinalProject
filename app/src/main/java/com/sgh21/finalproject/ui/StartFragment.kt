package com.sgh21.finalproject.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sgh21.finalproject.databinding.FragmentStartBinding

class StartFragment: Fragment() {

    private lateinit var startBinding: FragmentStartBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        startBinding = FragmentStartBinding.inflate(inflater, container, false)
        return startBinding.root
    }
}