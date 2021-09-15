package com.sgh21.finalproject.ui.resume

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sgh21.finalproject.data.Products
import com.sgh21.finalproject.databinding.FragmentResumeBinding

class ResumeFragment : Fragment() {

    private lateinit var resumeBinding: FragmentResumeBinding
    private lateinit var productsAdapter: productsAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        resumeBinding = FragmentResumeBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        productsAdapter = productsAdapter(onItemClicked = { onProductItemClicked(it)})
        resumeBinding.productRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ResumeFragment.context)
            adapter = productsAdapter
            setHasFixedSize(false)
        }
        resumeBinding.swipeRefresh.setOnRefreshListener {
            loadProducts()
            resumeBinding.swipeRefresh.isRefreshing = false
        }
        loadProducts()

        return resumeBinding.root
    }

    private fun loadProducts() {
        val db = Firebase.firestore
        db.collection("products").get().addOnSuccessListener { result ->
            val listProduct: MutableList<Products> = arrayListOf()
            val sellerId = auth.currentUser?.uid.toString()
            for(document in result){
                Log.d("Nombre", document.data.toString())
                val product: Products = document.toObject<Products>()
                if(product.sellerId == sellerId){
                    listProduct.add(product)
                }
            }
            productsAdapter.appendItems(listProduct)
        }
    }

    private fun onProductItemClicked(product: Products) {
        findNavController().navigate(ResumeFragmentDirections.actionNavResumeToEditProductFragment(product))
    }
}