package com.example.healingyukapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healingyukapp.databinding.CardFavouriteBinding
import com.squareup.picasso.Picasso

class FavouriteAdapter(private val favourites: ArrayList<Location>) : RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(val binding: CardFavouriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val binding = CardFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return favourites.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val location = favourites[position]
        with(holder.binding) {
            textFavouriteName.text = location.name
            textFavouriteCategory.text = location.category

            Picasso.get()
                .load(location.image_url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imgFavourite)

            // Listener untuk seluruh card
            root.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, LocationDetailActivity::class.java).apply {
                    putExtra(LocationDetailActivity.EXTRA_LOCATION, location)
                    // Tambahkan flag untuk menandakan ini dari halaman favorit
                    putExtra("FROM_FAVOURITE", true)
                }
                context.startActivity(intent)
            }
        }
    }
}