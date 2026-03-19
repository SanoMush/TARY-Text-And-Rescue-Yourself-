package com.sanomush.tari.data

data class EmergencyData(
    val kategori: String,
    val judul: String,
    val sumber: String,
    val tindakan: List<String>,
    val larangan: List<String>,
    val perlengkapan: List<String>
)