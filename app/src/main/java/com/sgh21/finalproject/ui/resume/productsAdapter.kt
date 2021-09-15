package com.sgh21.finalproject.ui.resume

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgh21.finalproject.R
import com.sgh21.finalproject.data.Products
import com.sgh21.finalproject.databinding.CardViewProductsItemBinding
import com.squareup.picasso.Picasso

class productsAdapter(
    private val onItemClicked: (Products) -> Unit,
) : RecyclerView.Adapter<productsAdapter.ViewHolder>() {

    private var listproduct: MutableList<Products> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_products_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listproduct[position])
        holder.itemView.setOnClickListener { onItemClicked(listproduct[position]) }
    }

    override fun getItemCount(): Int {
        return listproduct.size
    }

    fun appendItems(newItems: MutableList<Products>){
        listproduct.clear()
        listproduct.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val binding = CardViewProductsItemBinding.bind(view)
        fun bind(product: Products){
            with(binding){
                nameProductCardTextView.text = product.name
                priceProductCardTextView.text = product.price
                if(product.urlPictures != null){
                    Picasso.get().load(product.urlPictures!![0]).into(pictureCardImageView);
                }
            }

        }
    }
}