package com.example.healingyukapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healingyukapp.databinding.FragmentProfileBinding
import org.json.JSONObject

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchProfileData()
    }

    private fun fetchProfileData() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(context, "Sesi tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "https://ubaya.xyz/native/160422100/get_profile_data.php"
        val queue = Volley.newRequestQueue(requireActivity())

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if (jsonObj.getString("status") == "success") {
                        val data = jsonObj.getJSONObject("data")

                        // Set data ke komponen UI
                        binding.editTextProfileName.setText(data.getString("name"))
                        binding.editTextProfileEmail.setText(data.getString("email"))
                        binding.editTextProfileJoinDate.setText(data.getString("join_date"))
                        binding.editTextProfileTotalFav.setText(data.getInt("total_favourites").toString())
                    } else {
                        Toast.makeText(context, jsonObj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("ProfileFetchError", "JSON Parsing Error: ${e.message}")
                }
            },
            { error ->
                Log.e("ProfileFetchError", "Volley Error: ${error.toString()}")
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_id"] = userId.toString()
                return params
            }
        }
        queue.add(stringRequest)
    }
}