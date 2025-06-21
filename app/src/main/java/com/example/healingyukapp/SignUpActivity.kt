package com.example.healingyukapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.healingyukapp.databinding.ActivitySignUpBinding
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnSubmitSignUp.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        val email = binding.editTextEmailSignUp.text.toString().trim()
        val name = binding.editTextNameSignUp.text.toString().trim()
        val password = binding.editTextPasswordSignUp.text.toString().trim()
        val repeatPassword = binding.editTextRepeatPasswordSignUp.text.toString().trim()

        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            return // Hentikan fungsi jika ada yang kosong
        }

        if (password != repeatPassword) {
            Toast.makeText(this, "Password dan Ulangi Password tidak sama", Toast.LENGTH_SHORT).show()
            return // Hentikan fungsi jika password tidak cocok
        }

        val url = "http://192.168.100.175/healing_yuk_api/signup.php"

        val queue = Volley.newRequestQueue(this)

        // Buat StringRequest untuk metode POST
        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                // Tangani respon sukses dari server
                try {
                    val jsonObj = JSONObject(response)
                    val status = jsonObj.getString("status")
                    val message = jsonObj.getString("message")

                    if (status == "success") {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                        finish() // Kembali ke halaman Sign In setelah sukses
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Gagal memparsing data", Toast.LENGTH_SHORT).show()
                    Log.e("SignUpError", "Error parsing JSON: ${e.message}")
                }
            },
            { error ->
                // Tangani respon error dari server
                Toast.makeText(this, "Terjadi kesalahan koneksi: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("SignUpError", "Volley Error: ${error.message}")
            }) {

            // Override getParams untuk mengirim data sebagai POST body
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
                params["name"] = name
                params["password"] = password
                params["repeat_password"] = repeatPassword
                return params
            }
        }

        // Tambahkan request ke antrian
        queue.add(stringRequest)
    }
}