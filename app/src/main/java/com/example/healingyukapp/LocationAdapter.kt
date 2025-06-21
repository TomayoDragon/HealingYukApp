package com.example.healingyukapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healingyukapp.databinding.CardLocationBinding
import com.squareup.picasso.Picasso

class LocationAdapter(private val locations: ArrayList<Location>) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    class LocationViewHolder(val binding: CardLocationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = CardLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]

        with(holder.binding) {

            textLocationName.text = location.name
            textLocationCategory.text = location.category
            textShortDescription.text = location.short_description
            btnReadMore.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, LocationDetailActivity::class.java).apply {
                    // Mengirim seluruh object Location ke LocationDetailActivity
                    // Ini bisa dilakukan karena data class Location sudah @Parcelize
                    putExtra(LocationDetailActivity.EXTRA_LOCATION, location)
                }
                context.startActivity(intent)
            }

            Picasso.get()
                .load(location.image_url)
                .placeholder(R.drawable.error_load_image)
                .error(R.drawable.error_load_image)
                .into(imgLocation)
        }

    }
}