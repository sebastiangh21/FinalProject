package com.sgh21.finalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.sgh21.finalproject.R
import com.sgh21.finalproject.data.Products
import com.sgh21.finalproject.data.PurchasesAndSales
import com.sgh21.finalproject.data.User
import com.sgh21.finalproject.databinding.FragmentProductBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ProductFragment : Fragment() {

    private lateinit var productBinding: FragmentProductBinding
    private val args: ProductFragmentArgs by navArgs()
    private var idsFavorites: MutableList<String> = mutableListOf()
    private lateinit var auth: FirebaseAuth
    private lateinit var product: Products
    private var isFavorite = false
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        productBinding = FragmentProductBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        productBinding.addToFavoritesProductTextView.setOnClickListener {
            updateFavorite()
        }
        productBinding.buyProductButton.setOnClickListener {
            buyProduct()
        }
        return productBinding.root
    }

    private fun buyProduct() {
        val db = Firebase.firestore
        val document = db.collection("purchasesAndSales").document()
        val id = document.id
        val date = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.US)
        val formattedDate = sdf.format(date)
        val purchasesAndSales = PurchasesAndSales (
            id = id,
            productId = product.id,
            sellerId = product.sellerId,
            buyerId = userId,
            price = product.price,
            date = formattedDate.toString()
        )
        db.collection("purchasesAndSales").document(id)
            .set(purchasesAndSales)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireContext(),getString(R.string.successful_purchase), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("CreateInDB", "Error adding document", e)
            }
    }

    private fun updateFavorite() {
        isFavorite = !isFavorite
        if (isFavorite){
            idsFavorites.add(product.id.toString())
        }else{
            idsFavorites.remove(product.id.toString())
        }
        val documentUpdate = HashMap<String, Any>()
        documentUpdate["idsFavorites"] = idsFavorites
        val db = Firebase.firestore
        userId.let { it1 -> db.collection("users").document(it1).update(documentUpdate)}
        updateFavoriteView(isFavorite)
    }

    private fun updateFavoriteView(isFavorite: Boolean) {
        if(isFavorite){
            productBinding.addToFavoritesProductTextView.text = getString(R.string.remove_from_favorites)
            productBinding.addToFavoritesProductTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_favorite,  0, 0, 0)
        }else{
            productBinding.addToFavoritesProductTextView.text = getString(R.string.add_to_favorites)
            productBinding.addToFavoritesProductTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_favorite_border,  0, 0, 0)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        product = args.product
        updateInformation()
    }

    private fun updateInformation() {
        val db = Firebase.firestore
        userId = auth.currentUser?.uid.toString()

        db.collection("users").get().addOnSuccessListener { result ->
            var userExist = false
            for(document in result){
                val user: User = document.toObject<User>()
                if(user.id == userId){
                    userExist = true
                    idsFavorites = user.idsFavorites!!
                    if(idsFavorites.contains(product.id)) isFavorite = true
                    updateFavoriteView(isFavorite)
                    break
                }
            }
            if(!userExist){
                Toast.makeText(requireContext(),"Usuario no existe", Toast.LENGTH_SHORT).show()
            }
        }

        with(productBinding){
            if(product.urlPictures != null){
                Picasso.get().load(product.urlPictures!![0]).into(pictureProductImageView);
            }
            nameProductTextView.text = product.name
            priceProductTextView.text = product.price
            technicalSpecificationsProductTextView.text = product.features
            descriptionProductTextView.text = product.description
        }
    }
}