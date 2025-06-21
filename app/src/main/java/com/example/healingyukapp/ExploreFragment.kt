package com.example.healingyukapp

import android.content.Intent
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
import com.example.healingyukapp.databinding.FragmentExploreBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class ExploreFragment : Fragment() {

    private lateinit var binding: FragmentExploreBinding
    private var locations = ArrayList<Location>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup listener untuk FAB
        binding.fabAddLocation.setOnClickListener {
            val intent = Intent(activity, AddLocationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Panggil fungsi untuk mengambil data terbaru dari API setiap kali halaman ini ditampilkan
        fetchLocations()
    }

    private fun fetchLocations() {
        // Bersihkan list sebelum diisi data baru untuk mencegah duplikat
        locations.clear()

        val url = "http://192.168.100.175/healing_yuk_api/get_locations.php"
        // Gunakan `requireActivity()` untuk konteks yang non-null dan aman di fragment
        val queue = Volley.newRequestQueue(requireActivity())

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if (jsonObj.getString("status") == "success") {
                        val dataArray = jsonObj.getJSONArray("data")
                        val type = object : TypeToken<ArrayList<Location>>() {}.type
                        locations = Gson().fromJson(dataArray.toString(), type)
                        setupRecyclerView()
                        Log.d("ExploreFetch", "Data berhasil diperbarui: ${locations.size} item.")
                    } else {
                        // Jika tidak ada data, tetap setup recycler view dengan list kosong
                        setupRecyclerView()
                    }
                } catch (e: Exception) {
                    Log.e("ExploreFetch", "JSON Parsing Error: ${e.message}")
                    Toast.makeText(context, "Gagal mem-parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("ExploreFetch", "Volley Error: ${error.toString()}")
                Toast.makeText(context, "Kesalahan koneksi", Toast.LENGTH_SHORT).show()
            })

        queue.add(stringRequest)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewLocations.layoutManager = LinearLayoutManager(context)
        val adapter = LocationAdapter(locations)
        binding.recyclerViewLocations.adapter = adapter
    }
}