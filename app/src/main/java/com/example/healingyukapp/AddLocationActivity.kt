package com.example.healingyukapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healingyukapp.databinding.ActivityAddLocationBinding
import org.json.JSONObject

class AddLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener untuk tombol kembali
        binding.btnBackAdd.setOnClickListener {
            finish()
        }

        // Listener untuk tombol submit "Simpan Lokasi"
        binding.btnSubmitAdd.setOnClickListener {
            submitLocationData()
        }
    }

    private fun submitLocationData() {
        val name = binding.editTextName.text.toString().trim()
        val imageUrl = binding.editTextImageUrl.text.toString().trim()
        val shortDesc = binding.editTextShortDesc.text.toString().trim()
        val fullDesc = binding.editTextFullDesc.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()

        if (name.isEmpty() || imageUrl.isEmpty() || shortDesc.isEmpty() || fullDesc.isEmpty()) {
            Toast.makeText(this, "Semua kolom teks harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.100.175/healing_yuk_api/add_location.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    val status = jsonObj.getString("status")
                    val message = jsonObj.getString("message")

                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    if (status == "success") {
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("AddLocationError", "JSON Parsing Error: ${e.message}")
                    Toast.makeText(this, "Gagal mem-parsing data dari server", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("AddLocationError", "Volley Error: ${error.toString()}")
                Toast.makeText(this, "Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["name"] = name
                params["image_url"] = imageUrl
                params["short_description"] = shortDesc
                params["category"] = category
                params["full_description"] = fullDesc
                return params
            }
        }
        queue.add(stringRequest)
    }
}