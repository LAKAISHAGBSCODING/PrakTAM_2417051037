package com.example.praktam_2417051037.data.api

import com.example.praktam_2417051037.data.model.Language
import com.example.praktam_2417051037.data.model.QuizData
import retrofit2.http.GET

interface ApiService {
    @GET("fbc790816c2f57af29b88bef8b89d189/raw/datacodingan.txt")
    suspend fun getLanguages(): List<Language>

    @GET("78b077f75088ff93bfb050e53cefa72f/raw/gistfile1.txt")
    suspend fun getQuizzes(): List<QuizData>
}