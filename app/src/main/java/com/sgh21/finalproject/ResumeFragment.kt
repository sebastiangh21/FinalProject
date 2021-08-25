package com.sgh21.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sgh21.finalproject.databinding.FragmentResumeBinding

class ResumeFragment : Fragment() {

    private lateinit var resumeBinding: FragmentResumeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        resumeBinding = FragmentResumeBinding.inflate(inflater, container, false)
        return resumeBinding.root
    }
}