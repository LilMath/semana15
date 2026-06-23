package com.example.myapplication

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Task(
    val id: String? = null,
    val nome: String? = null,
    val concluida: Boolean = false
)
