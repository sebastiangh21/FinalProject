package com.sgh21.finalproject.ui

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
import com.sgh21.finalproject.data.User
import com.sgh21.finalproject.databinding.FragmentFavoriteBinding
import com.sgh21.finalproject.ui.resume.productsAdapter

class FavoriteFragment : Fragment() {

    private lateinit var favoriteBinding: FragmentFavoriteBinding
    private lateinit var productsAdapter: productsAdapter
    private var idsFavorites: MutableList<String> = mutableListOf()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        favoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        productsAdapter = productsAdapter(onItemClicked = { onProductItemClicked(it)})
        favoriteBinding.favoriteRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoriteFragment.context)
            adapter = productsAdapter
            setHasFixedSize(false)
        }
        loadFavorites()
        return favoriteBinding.root
    }

    private fun loadProducts() {
        val db = Firebase.firestore
        db.collection("products").get().addOnSuccessListener { result ->
            val listProduct: MutableList<Products> = arrayListOf()
            for(document in result){
                Log.d("Nombre", document.data.toString())
                val product: Products = document.toObject<Products>()
                if(idsFavorites.contains(product.id))listProduct.add(product)
            }
            productsAdapter.appendItems(listProduct)
        }
    }

    private fun loadFavorites() {
        val db = Firebase.firestore
        val userId = auth.currentUser?.uid.toString()
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val user: User = document.toObject()
                if (user.id == userId) {
                    idsFavorites = user.idsFavorites!!
                    loadProducts()
                    break
                }
            }
        }
    }

    private fun onProductItemClicked(products: Products) {
        findNavController().navigate(FavoriteFragmentDirections.actionNavFavoriteToProductFragment(products))
    }

}