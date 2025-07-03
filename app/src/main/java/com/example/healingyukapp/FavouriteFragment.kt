package com.example.healingyukapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healingyukapp.databinding.FragmentFavouriteBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class FavouriteFragment : Fragment() {

    private lateinit var binding: FragmentFavouriteBinding
    private var favourites = ArrayList<Location>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Gunakan onResume agar daftar favorit selalu diperbarui setiap kali tab ini ditampilkan
    override fun onResume() {
        super.onResume()
        fetchFavourites()
    }

    private fun fetchFavourites() {
        // Ambil user_id dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(context, "Sesi tidak ditemukan, silakan login kembali.", Toast.LENGTH_SHORT).show()
            return
        }

        // Kosongkan list sebelum diisi data baru
        favourites.clear()

        val url = "https://ubaya.xyz/native/160422100/get_favourites.php"
        val queue = Volley.newRequestQueue(requireActivity())

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if (jsonObj.getString("status") == "success") {
                        val dataArray = jsonObj.getJSONArray("data")
                        val type = object : TypeToken<ArrayList<Location>>() {}.type
                        favourites = Gson().fromJson(dataArray.toString(), type)
                        Log.d("FavouriteFetch", "Data favorit berhasil diambil: ${favourites.size} item.")
                    } else {
                        // Jika status error (misal: "belum punya favorit"), biarkan list 'favourites' kosong
                        Log.d("FavouriteFetch", "Pesan dari server: ${jsonObj.getString("message")}")
                    }
                    // Tetap setup recycler view baik ada data maupun tidak
                    setupRecyclerView()
                } catch (e: Exception) {
                    Log.e("FavouriteFetch", "JSON Parsing Error: ${e.message}")
                }
            },
            { error ->
                Log.e("FavouriteFetch", "Volley Error: ${error.toString()}")
            }) {

            // Kirim user_id sebagai parameter POST
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_id"] = userId.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewFavourites.layoutManager = LinearLayoutManager(context)
        // Gunakan FavouriteAdapter
        val adapter = FavouriteAdapter(favourites)
        binding.recyclerViewFavourites.adapter = adapter
    }
}