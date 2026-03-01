package com.example.praktam_2417051037.model

import androidx.annotation.DrawableRes

data class Language(
    val nama: String,
    val deskripsi: String,
    @DrawableRes val imageRes: Int
)