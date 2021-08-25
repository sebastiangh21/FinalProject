package com.sgh21.finalproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sgh21.finalproject.databinding.FragmentAccountBinding
import com.sgh21.finalproject.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private lateinit var historyBinding: FragmentHistoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        historyBinding = FragmentHistoryBinding.inflate(inflater, container, false)
        return historyBinding.root
    }
}