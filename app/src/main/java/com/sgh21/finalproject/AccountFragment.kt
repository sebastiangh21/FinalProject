package com.sgh21.finalproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sgh21.finalproject.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private lateinit var accountBinding: FragmentAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        accountBinding = FragmentAccountBinding.inflate(inflater, container, false)
        return accountBinding.root
    }
}