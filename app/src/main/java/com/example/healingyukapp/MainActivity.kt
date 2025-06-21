package com.example.healingyukapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healingyukapp.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // LANGKAH 5: Cek Sesi Login Saat Aplikasi Dibuka
        checkSession()

        // Listener untuk tombol Sign Up
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Listener untuk tombol Submit Sign In
        binding.btnSubmit.setOnClickListener {
            signInUser()
        }
    }

    private fun checkSession() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            // Jika sudah login, langsung ke HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Tutup MainActivity agar tidak bisa kembali ke halaman login
        }
    }

    private fun signInUser() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.100.175/healing_yuk_api/signin.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    val status = jsonObj.getString("status")
                    val message = jsonObj.getString("message")

                    if (status == "success") {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                        // LANGKAH 3: Simpan data user ke SharedPreferences
                        val userData = jsonObj.getJSONObject("data")
                        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        editor.putBoolean("is_logged_in", true)
                        editor.putInt("user_id", userData.getInt("id"))
                        editor.putString("user_name", userData.getString("name"))
                        editor.putString("user_email", userData.getString("email"))
                        editor.apply()

                        // LANGKAH 4: Pindah ke HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Tutup MainActivity

                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Gagal memparsing data", Toast.LENGTH_SHORT).show()
                    Log.e("SignInError", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                Toast.makeText(this, "Terjadi kesalahan koneksi: ${error.toString()}", Toast.LENGTH_LONG).show()
                Log.e("SignInError", "Volley Error: ${error.toString()}")
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
                params["password"] = password
                return params
            }
        }
        queue.add(stringRequest)
    }
}