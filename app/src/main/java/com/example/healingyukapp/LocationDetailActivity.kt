package com.example.healingyukapp

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healingyukapp.databinding.ActivityLocationDetailBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject

class LocationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationDetailBinding
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_LOCATION, Location::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_LOCATION)
        }

        // Baca flag untuk tahu kita datang dari halaman mana
        val fromFavourite = intent.getBooleanExtra("FROM_FAVOURITE", false)

        location?.let { displayLocationDetails(it, fromFavourite) }
    }

    private fun displayLocationDetails(loc: Location, fromFavourite: Boolean) {
        binding.toolbarLayout.title = loc.name
        binding.textDetailCategory.text = loc.category
        binding.textDetailFullDesc.text = loc.full_description

        Picasso.get()
            .load(loc.image_url)
            .into(binding.imgDetail)

        // Logika untuk menentukan fungsi tombol
        if (fromFavourite) {
            // Jika datang dari halaman favorit
            binding.btnAddToFavourite.text = "Remove from Favourite"
            binding.btnAddToFavourite.setOnClickListener {
                removeFromFavourites(loc.id)
            }
        } else {
            // Jika datang dari halaman explore
            binding.btnAddToFavourite.text = "Add to Favourite"
            binding.btnAddToFavourite.setOnClickListener {
                addToFavourites(loc.id)
            }
        }
    }

    private fun addToFavourites(locationId: Int) {
        // ... (kode ini sama seperti sebelumnya, tidak perlu diubah)
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId == -1) { return }

        val url = "https://ubaya.xyz/native/160422100/add_favourite.php"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    val message = jsonObj.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    if(jsonObj.getString("status") == "success"){
                        binding.btnAddToFavourite.isEnabled = false
                        binding.btnAddToFavourite.text = "Sudah di Favorit"
                    }
                } catch (e: Exception) { Log.e("FavouriteError", "JSON Parsing Error: ${e.message}") }
            },
            { error -> Log.e("FavouriteError", "Volley Error: ${error.toString()}") }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_id"] = userId.toString()
                params["location_id"] = locationId.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun removeFromFavourites(locationId: Int) {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId == -1) { return }

        val url = "https://ubaya.xyz/native/160422100/remove_favourite.php"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    val message = jsonObj.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    if(jsonObj.getString("status") == "success"){
                        // Jika berhasil dihapus, tutup halaman detail
                        finish()
                    }
                } catch (e: Exception) { Log.e("RemoveFavouriteError", "JSON Parsing Error: ${e.message}") }
            },
            { error -> Log.e("RemoveFavouriteError", "Volley Error: ${error.toString()}") }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_id"] = userId.toString()
                params["location_id"] = locationId.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_LOCATION = "extra_location"
    }
}