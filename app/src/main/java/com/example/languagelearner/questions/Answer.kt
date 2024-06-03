package com.example.languagelearner.questions

import android.media.Image

data class Answer(
    val answerLabel: String,
    val image: Image,
    val isCorrect: Boolean
)