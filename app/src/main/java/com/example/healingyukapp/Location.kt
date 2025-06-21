package com.example.healingyukapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Anotasi @Parcelize digunakan agar object ini bisa dikirim antar activity/fragment.
// Pastikan plugin 'kotlin-parcelize' sudah ada di build.gradle Anda.
@Parcelize
data class Location(
    val id: Int,
    val name: String,
    val image_url: String,
    val short_description: String,
    val category: String,
    val full_description: String
) : Parcelable