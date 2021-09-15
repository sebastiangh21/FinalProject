package com.sgh21.finalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sgh21.finalproject.data.Products
import com.sgh21.finalproject.databinding.FragmentSearchBinding
import com.sgh21.finalproject.ui.resume.productsAdapter

class SearchFragment : Fragment() {

    private lateinit var searchBinding: FragmentSearchBinding
    private lateinit var productsAdapter: productsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false)

        productsAdapter = productsAdapter(onItemClicked = { onProductItemClicked(it)})
        searchBinding.productsSearchRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchFragment.context)
            adapter = productsAdapter
            setHasFixedSize(false)
        }
        loadProducts()

        return searchBinding.root
    }

    private fun loadProducts() {
        val db = Firebase.firestore
        db.collection("products").get().addOnSuccessListener { result ->
            val listProduct: MutableList<Products> = arrayListOf()
            for(document in result){
                Log.d("Nombre", document.data.toString())
                val product: Products = document.toObject<Products>()
                listProduct.add(product)
            }
            productsAdapter.appendItems(listProduct)
        }
    }

    private fun onProductItemClicked(product: Products) {
        findNavController().navigate(SearchFragmentDirections.actionNavSearchToProductFragment(product))
    }
}