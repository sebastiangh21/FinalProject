package com.sgh21.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sgh21.finalproject.databinding.FragmentPublishBinding

class PublishFragment : Fragment() {

    private lateinit var publishBinding: FragmentPublishBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        publishBinding = FragmentPublishBinding.inflate(inflater, container, false)
        return publishBinding.root
    }
}