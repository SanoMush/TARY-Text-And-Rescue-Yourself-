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

    private var engine: InferenceEngine? = null
    private val jsonHelper = JsonFallbackHelper(context)

    // ── KOTAK INGATAN (Cukup 3 saja, biar AI gak mabuk) ──
    private val chatHistory = mutableListOf<Pair<String, String>>()

    fun addChatHistory(userMsg: String, aiMsg: String) {
        if (aiMsg.trim().isNotEmpty()) {
            chatHistory.add(Pair(userMsg, aiMsg))
            if (chatHistory.size > 3) {
                chatHistory.removeAt(0)
            }
        }
    }

    suspend fun loadModel() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("TARY", "Memanaskan mesin GGUF...")
                engine = AiChat.getInferenceEngine(context)

                val internalFile = java.io.File(context.filesDir, "gemma-3-1b-it-q4_0.gguf")

                if (!internalFile.exists()) {
                    context.assets.open("gemma-3-1b-it-q4_0.gguf").use { input ->
                        internalFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
                engine?.loadModel(internalFile.path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun generateResponse(userMessage: String): Flow<String> {
        val inference = engine ?: return flow { emit("Sistem TARY belum siap.") }
        val database = jsonHelper.loadEmergencyData()

        val queryLower = userMessage.lowercase()
        val queryWords = queryLower.split(Regex("\\W+"))

        val matchedData = database.firstOrNull { data ->
            val judulLower = data.judul.lowercase()
            val judulWords = judulLower.split(Regex("\\W+")).filter { it.length > 3 }
            judulWords.any { word -> queryWords.contains(word) }
        }

        // ── KONDISI 1: DATA ADA DI JSON -> LANGSUNG CETAK (BYPASS AI) ──
        // Ini bikin jawaban 100% valid dari PMI/BNPB, instan, dan anti-error!
        if (matchedData != null) {
            return flow {
                emit("📚 [SOP RESMI ${matchedData.sumber}]\n")
                emit("${matchedData.judul}\n\n")
                emit("TINDAKAN SEGERA:\n")
                matchedData.tindakan.forEach { emit("- $it\n") }

                if (matchedData.larangan.isNotEmpty()) {
                    emit("\nLARANGAN:\n")
                    matchedData.larangan.forEach { emit("- $it\n") }
                }
            }
        }

        // ── KONDISI 2: DATA GAK ADA -> PANGGIL AI RESPON SINGKAT ──
        // Ambil 1 chat terakhir aja biar obrolan tetap nyambung tanpa bikin AI pusing
        var historyText = ""
        if (chatHistory.isNotEmpty()) {
            val lastChat = chatHistory.last()
            historyText = "Konteks sebelumnya: Pasien mengeluh '${lastChat.first}'\n"
        }

        val finalPrompt = """
            Anda ahli P3K. DILARANG MENGUCAPKAN SALAM!
            $historyText
            Keluhan darurat saat ini: "$userMessage"
            
            Tugas: Berikan solusi P3K atau penyelamatan yang TEPAT SASARAN dan spesifik! 
            Wajib gunakan format berikut dengan kalimat yang sangat pendek:
            1. [Langkah pertolongan pertama secara medis/logis]
            2. [Tindakan pengamanan lanjutan]
            3. [Jika butuh evakuasi/gelap/nyasar, suruh tekan tombol Senter/Kompas/SOS merah di layar]
            
            Jawaban:
        """.trimIndent()

        return try {
            inference.sendUserPrompt(finalPrompt)
                .catch { e -> emit("\n[Sistem TARY sedang gangguan]") }
        } catch (e: Exception) {
            e.printStackTrace()
            flow { emit("Gagal memanggil mesin TARY.") }
        }
    }

    fun unloadModel() {
        try {
            engine?.destroy()
            engine = null
        } catch (e: Exception) {}
    }
}