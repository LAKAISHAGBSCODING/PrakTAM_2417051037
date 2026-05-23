package com.example.praktam_2417051037.data.repository

import com.example.praktam_2417051037.data.api.RetrofitClient
import com.example.praktam_2417051037.data.model.Language
import com.example.praktam_2417051037.data.model.QuizData

class LanguageRepository {
    suspend fun getLanguages(): List<Language> {
        return try {
            RetrofitClient.instance.getLanguages()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getQuizzes(): List<QuizData> {
        return try {
            RetrofitClient.instance.getQuizzes()
        } catch (e: Exception) {
            emptyList()
        }
    }
}