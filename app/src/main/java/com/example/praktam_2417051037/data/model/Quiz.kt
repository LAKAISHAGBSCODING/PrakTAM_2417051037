package com.example.praktam_2417051037.data.model

import com.google.gson.annotations.SerializedName

data class QuizData(
    val bahasa: String,
    val materi: String,
    val kuis: List<Question>
)

data class Question(
    val soal: String,
    val pilihan: List<String>,
    @SerializedName("jawaban_benar")
    val jawabanBenar: Int
)