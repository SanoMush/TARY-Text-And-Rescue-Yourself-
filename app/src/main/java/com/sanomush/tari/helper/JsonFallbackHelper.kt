package com.sanomush.tari.helper

import android.content.Context
import com.sanomush.tari.data.EmergencyData
import org.json.JSONArray

object JsonFallbackHelper {

    private val emergencyDatabase = mutableListOf<EmergencyData>()
    fun initDatabase(context: Context) {
        if (emergencyDatabase.isNotEmpty()) return

        try {
            val inputStream = context.assets.open("emergency_data.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val keywordsArray = jsonObject.getJSONArray("keywords")
                val keywordsList = mutableListOf<String>()
                for (j in 0 until keywordsArray.length()) {
                    keywordsList.add(keywordsArray.getString(j).lowercase())
                }
                val instruction = jsonObject.getString("instruction")
                emergencyDatabase.add(EmergencyData(keywordsList, instruction))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun searchInstruction(query: String): String {
        val lowerQuery = query.lowercase()

        for (data in emergencyDatabase) {
            for (keyword in data.keywords) {
                if (lowerQuery.contains(keyword)) {
                    return data.instruction
                }
            }
        }
        return "Maaf, panduan untuk situasi tersebut belum tersedia di database darurat offline kami. Cobalah gunakan kata kunci lain seperti 'luka', 'patah tulang', atau 'terjebak'."
    }
}