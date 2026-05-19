package com.example.praktam_2417051037.data.api

import com.example.praktam_2417051037.data.model.Language
import retrofit2.http.GET

interface ApiService {
    @GET("fbc790816c2f57af29b88bef8b89d189/raw/27464feba9148bde4d9afa19f0ca8d7b9489f3b0/datacodingan.txt")
    suspend fun getLanguages(): List<Language>
}