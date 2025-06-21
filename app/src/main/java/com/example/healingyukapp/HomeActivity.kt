package com.example.healingyukapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.healingyukapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val fragments: ArrayList<Fragment> = ArrayList()
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Mengatur Toolbar sebagai Action Bar utama untuk Activity ini.
        setSupportActionBar(binding.toolbar)

        // 2. Menyiapkan ActionBarDrawerToggle untuk ikon hamburger dan fungsionalitas drawer.
        // Ini adalah implementasi langsung dari materi Minggu ke-14.
        drawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.app_name, R.string.app_name)
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.isDrawerIndicatorEnabled = true // Memastikan ikon yang muncul adalah hamburger menu.
        drawerToggle.syncState() // Sinkronisasi state untuk menampilkan ikon.
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Memungkinkan ikon untuk diklik.

        // 3. Menyiapkan ViewPager dan menghubungkannya dengan adapter.
        setupViewPager()

        // 4. Mengatur semua listener untuk navigasi (BottomNav, ViewPager, dan Drawer).
        setupNavigationListeners()

        // 5. Mengatur teks header di dalam drawer.
        setupDrawerHeader()
    }

    // Fungsi ini wajib ada agar klik pada hamburger icon dapat ditangani oleh drawerToggle.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Mengisi ViewPager dengan fragment yang dibutuhkan.
    private fun setupViewPager() {
        fragments.add(ExploreFragment())
        fragments.add(FavouriteFragment())
        fragments.add(ProfileFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        binding.viewPager.adapter = adapter
    }

    // Mengatur interaksi antara semua komponen navigasi.
    private fun setupNavigationListeners() {
        // Listener untuk BottomNavigationView (sesuai materi Minggu ke-9).
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemExplore -> binding.viewPager.currentItem = 0
                R.id.itemFavourites -> binding.viewPager.currentItem = 1
                R.id.itemProfile -> binding.viewPager.currentItem = 2
            }
            true
        }

        // Listener untuk ViewPager agar bisa meng-update BottomNav (sesuai materi Minggu ke-9).
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomNav.selectedItemId = binding.bottomNav.menu.getItem(position).itemId
            }
        })

        // Listener untuk menu di dalam Navigation Drawer (sesuai materi Minggu ke-14).
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.itemSignOut -> {
                    // Hapus sesi dari SharedPreferences.
                    val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    // Kembali ke MainActivity (halaman login).
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup HomeActivity.
                }
                R.id.itemChangePassword -> {
                    // Pindah ke ChangePasswordActivity.
                    val intent = Intent(this, ChangePasswordActivity::class.java)
                    startActivity(intent)
                }
            }
            // Tutup drawer setelah salah satu item diklik.
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    // Mengambil nama pengguna dari SharedPreferences dan menampilkannya.
    private fun setupDrawerHeader() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("user_name", "User")

        val headerView = binding.navView.getHeaderView(0)
        val textHeaderName = headerView.findViewById<TextView>(R.id.textHeaderName)
        textHeaderName.text = "Welcome, $username"
    }
}