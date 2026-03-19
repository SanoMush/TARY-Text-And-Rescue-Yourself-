package com.sanomush.tari.helper

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.sanomush.tari.data.EmergencyData
import java.io.InputStreamReader

class JsonFallbackHelper(private val context: Context) {

    // Fungsi sakti buat narik semua data dari emergency_data.json
    fun loadEmergencyData(): List<EmergencyData> {
        return try {
            // Buka file JSON dari folder assets
            val inputStream = context.assets.open("emergency_data.json")
            val reader = InputStreamReader(inputStream)

            // Terjemahkan JSON ke dalam List<EmergencyData> pakai Gson
            val listType = object : TypeToken<List<EmergencyData>>() {}.type
            val data: List<EmergencyData> = Gson().fromJson(reader, listType)

            reader.close()
            Log.d("TARY_JSON", "Berhasil narik ${data.size} data darurat dari JSON!")
            data
        } catch (e: Exception) {
            Log.e("TARY_JSON", "Gagal baca JSON: ${e.message}")
            emptyList() // Kalau gagal, kembalikan list kosong biar aplikasi gak crash
        }
    }
}