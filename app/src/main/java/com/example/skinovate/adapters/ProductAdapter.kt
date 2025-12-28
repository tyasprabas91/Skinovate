package com.example.skinovate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skinovate.R
import com.example.skinovate.data.Product // Import your new Model

class ProductAdapter(
    private var productList: List<Product>,
    private val onProductClick: (Product) -> Unit // Click listener for later
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // These IDs (productImage, productName, etc.) must exist in your XML layout!
        val imageView: ImageView = itemView.findViewById(R.id.productImage)
        val nameText: TextView = itemView.findViewById(R.id.productName)
        val categoryText: TextView = itemView.findViewById(R.id.productCategory)
        val priceText: TextView = itemView.findViewById(R.id.productPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Change 'item_product_card' to whatever your actual XML layout file is named
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.nameText.text = product.name
        holder.categoryText.text = product.category
        holder.priceText.text = "$${product.price}" // Simple formatting
        holder.imageView.setImageResource(product.imageResId)

        // Handle clicks (Navigation to detail page)
        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount() = productList.size

    // Helper to update data dynamically
    fun updateData(newProducts: List<Product>) {
        productList = newProducts
        notifyDataSetChanged()
    }
}