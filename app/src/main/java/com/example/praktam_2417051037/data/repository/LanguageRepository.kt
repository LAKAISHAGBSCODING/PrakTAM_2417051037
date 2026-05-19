package com.example.praktam_2417051037.data.repository

import com.example.praktam_2417051037.data.api.RetrofitClient
import com.example.praktam_2417051037.data.model.Language

class LanguageRepository {
    suspend fun getLanguages(): List<Language> {
        return RetrofitClient.instance.getLanguages()
    }
}