package com.sgh21.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sgh21.finalproject.databinding.FragmentPurchasesBinding

class PurchasesFragment : Fragment() {

    private lateinit var purchasesBinding: FragmentPurchasesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        purchasesBinding = FragmentPurchasesBinding.inflate(inflater, container, false)
        return purchasesBinding.root
    }

}