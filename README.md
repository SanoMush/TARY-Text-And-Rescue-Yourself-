<div align="center">

# 🚨 TARY – Text And Rescue Yourself 🚨
**AI-Powered Offline Emergency Assistant for Disaster Situations**

</div>

> **TARY** adalah aplikasi mobile Android yang dirancang untuk membantu masyarakat mendapatkan panduan darurat dan komunikasi bantuan secara cepat, bahkan ketika **tidak ada koneksi internet**.

Aplikasi ini dibuat untuk mendukung kesiapsiagaan bencana di Indonesia, negara yang memiliki tingkat kerentanan bencana alam yang tinggi. TARY memanfaatkan kombinasi AI *on-device*, *offline emergency knowledge base*, dan fitur SOS otomatis untuk membantu pengguna bertindak cepat dalam situasi kritis.

---

## 🌏 Latar Belakang

Indonesia merupakan salah satu negara paling rawan bencana di dunia. Ketika bencana terjadi, jaringan komunikasi sering terganggu sehingga masyarakat kesulitan mendapatkan informasi atau meminta bantuan. 

TARY hadir sebagai **Emergency Assistant berbasis AI** yang tetap dapat berfungsi secara *offline*, sehingga pengguna dapat:
- 🩹 Memperoleh panduan pertolongan pertama.
- 🆘 Mengirim pesan darurat.
- 🦺 Mendapatkan instruksi keselamatan.
- 🔗 Tetap terhubung dengan orang lain bahkan ketika internet tidak tersedia.

---

## 🧠 Teknologi AI & Model (*On-Device Inference*)

Aplikasi ini ditenagai oleh *Large Language Model* (LLM) yang berjalan sepenuhnya secara *offline* di dalam perangkat. TARY tidak memerlukan koneksi internet sama sekali saat memproses kueri pengguna di kondisi darurat.

- **Model yang Digunakan:** **[Gemma-3-1B-Instruct](https://huggingface.co/franklynical/gemma3b-1t-q4)** (dalam format terkuantisasi `gemma-3-1b-it-q4_0.gguf`).
- **Sistem Inferensi:** Menggunakan *Inference Engine* lokal (`AiChat`) dengan optimalisasi memori (membatasi *chat history* maksimal 3 interaksi terakhir) agar performa *generate* teks tetap sangat cepat.

---

## 📚 Offline Emergency Knowledge Base

Untuk memastikan akurasi tingkat tinggi, menghindari halusinasi AI, dan menjamin keselamatan pengguna, TARY mengimplementasikan arsitektur logika **Hybrid**:

1. ⚡ **SOP Resmi (*Bypass AI*):** Jika keluhan darurat memiliki kecocokan (*keyword matching*) dengan database lokal, aplikasi akan langsung memunculkan Standar Operasional Prosedur (SOP) baku secara instan.
2. 🤖 **AI *Fallback*:** Jika keluhan spesifik tidak ditemukan di database,
