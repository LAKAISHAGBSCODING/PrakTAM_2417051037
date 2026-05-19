package com.example.praktam_2417051037.data.model

import com.google.gson.annotations.SerializedName

data class Language(
    val nama: String,
    val deskripsi: String,
    val kategori: String,
    @SerializedName("image_url")
    val imageUrl: String?
)