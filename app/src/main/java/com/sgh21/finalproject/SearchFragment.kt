package com.sgh21.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sgh21.finalproject.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var searchBinding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return searchBinding.root
    }
}