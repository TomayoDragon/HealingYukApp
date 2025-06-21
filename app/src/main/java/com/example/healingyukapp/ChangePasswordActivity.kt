package com.example.healingyukapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healingyukapp.databinding.ActivityChangePasswordBinding
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar dengan tombol kembali
        setSupportActionBar(binding.toolbarChangePassword)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbarChangePassword.setNavigationOnClickListener {
            finish()
        }

        binding.btnSubmitChangePassword.setOnClickListener {
            performChangePassword()
        }
    }

    private fun performChangePassword() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("user_id", -1)

        val oldPassword = binding.editTextOldPassword.text.toString()
        val newPassword = binding.editTextNewPassword.text.toString()
        val repeatPassword = binding.editTextRepeatPassword.text.toString()

        // Validasi client-side
        if (oldPassword.isEmpty() || newPassword.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(this, "Semua kolom password harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPassword != repeatPassword) {
            Toast.makeText(this, "Password baru dan ulangi password tidak cocok.", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.100.175/healing_yuk_api/change_password.php"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    val message = jsonObj.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    if (jsonObj.getString("status") == "success") {
                        finish() // Kembali ke halaman Home jika sukses
                    }
                } catch (e: Exception) { Log.e("ChangePassError", "JSON Parsing: ${e.message}") }
            },
            { error -> Log.e("ChangePassError", "Volley: ${error.toString()}") }) {

            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_id"] = userId.toString()
                params["old_password"] = oldPassword
                params["new_password"] = newPassword
                return params
            }
        }
        queue.add(stringRequest)
    }
}