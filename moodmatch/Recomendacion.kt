package com.example.moodmatch

import android.adservices.adid.AdId
import android.media.Image
import androidx.compose.ui.text.LinkAnnotation

data class Recomendacion(
    val id: Int = 0,
    val tipo: String,
    val titulo: String,
    val descripcion: String,
    val estadoAnimoId: Int,
    val imagen: String,
    val plataformas: String,
)
