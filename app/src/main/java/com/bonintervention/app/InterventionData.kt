package com.bonintervention.app

import android.graphics.Bitmap

data class InterventionData(
    val data: String,
    val ora: String,
    val service: String,
    val idSmv: String,
    val equipement: String,
    val description: String,
    val declarant: String,
    val photo: Bitmap?
)
