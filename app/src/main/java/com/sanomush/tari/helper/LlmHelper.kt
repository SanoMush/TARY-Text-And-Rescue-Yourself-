package com.sanomush.tari.helper

import android.content.Context
import android.util.Log
import com.arm.aichat.AiChat
import com.arm.aichat.InferenceEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LlmHelper(private val context: Context) {

    // Mesin AI dari library ARM
    private var engine: InferenceEngine? = null

    // 1. Load Model pakai Coroutine biar UI gak nge-freeze
    suspend fun loadModel() {
        withContext(Dispatchers.IO) { // Pakai IO karena kita mau baca/tulis file besar
            try {
                Log.d("TARY", "Memanaskan mesin GGUF...")
                engine = AiChat.getInferenceEngine(context)

                // Lokasi "Kamar Pribadi" aplikasi TARY
                val internalFile = java.io.File(context.filesDir, "gemma-3-1b-it-q4_0.gguf")

                // Kalau file belum ada di kamar pribadi TARY, kita copy dari folder Assets!
                if (!internalFile.exists()) {
                    Log.d("TARY", "Mengekstrak file AI ke sistem TARY... (Tunggu 1-2 menit ya!)")

                    context.assets.open("gemma-3-1b-it-q4_0.gguf").use { input ->
                        internalFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d("TARY", "Ekstrak file AI sukses!")
                } else {
                    Log.d("TARY", "File AI sudah ada, langsung dipanaskan!")
                }

                // Load mesinnya pakai jalur kamar pribadi!
                engine?.loadModel(internalFile.path)
                Log.d("TARY", "TARY: Model berhasil di-load dan SIAP DIGUNAKAN!")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TARY", "TARY: Gagal memanaskan mesin! Error: ${e.message}")
            }
        }
    }

    // 2. Generate response pakai Flow biar munculnya ngetik kata per kata
    fun generateResponse(userMessage: String): Flow<String> {
        val inference = engine ?: return flow { emit("Sistem TARY belum siap. Tunggu loading selesai.") }

        // RAHASIA TARY: Prompt bersih tanpa tag yang bikin bingung, plus Persona TARYUS!
        val finalPrompt = """
            Kamu adalah TARY, asisten darurat P3K. Jawab keluhan dengan sangat singkat, pakai bahasa sehari-hari. 
            Wajib selalu awali jawaban kamu dengan kalimat: "Halo TARYUS, jangan panik! Lakukan langkah-langkah ini:"
            
            Contoh 1:
            Keluhan: Saya mimisan terus nih.
            Jawaban: Halo TARYUS, jangan panik! Lakukan langkah-langkah ini:
            1. Duduk tegak dan condongkan badan ke depan.
            2. Pencet cuping hidung selama 10 menit.
            3. Jangan berbaring atau mendongak ke atas.
            
            Contoh 2:
            Keluhan: Kesiram air panas di dapur.
            Jawaban: Halo TARYUS, jangan panik! Lakukan langkah-langkah ini:
            1. Segera aliri luka pakai air keran biasa selama 15 menit.
            2. Jangan diolesin odol, kecap, atau mentega!
            3. Tutup pakai kain kasa bersih atau plastik wrap.
            
            Sekarang jawab keluhan darurat ini tanpa pakai istilah medis yang rumit:
            Keluhan: $userMessage
            Jawaban: 
        """.trimIndent()

        return try {
            // Minta engine membalas dan alirkan datanya (Flow)
            inference.sendUserPrompt(finalPrompt)
                .catch { e ->
                    Log.e("TARY", "Error saat generasi", e)
                    emit("\n[Sistem TARY sedang gangguan]")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            flow { emit("Gagal memanggil mesin TARY.") }
        }
    }

    // 3. Wajib panggil ini pas aplikasi ditutup biar RAM HP balik lega
    fun unloadModel() {
        try {
            engine?.destroy()
            engine = null
            Log.d("TARY", "Mesin GGUF berhasil dimatikan.")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}