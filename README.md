🚨 TARY – Text And Rescue Yourself🚨

AI-Powered Offline Emergency Assistant for Disaster Situations

TARY adalah aplikasi mobile Android yang dirancang untuk membantu masyarakat mendapatkan panduan darurat dan komunikasi bantuan secara cepat, bahkan ketika tidak ada koneksi internet.

Aplikasi ini dibuat untuk mendukung kesiapsiagaan bencana di Indonesia, negara yang memiliki tingkat kerentanan bencana alam yang tinggi.

TARY memanfaatkan kombinasi AI on-device, offline emergency knowledge base, dan fitur SOS otomatis untuk membantu pengguna bertindak cepat dalam situasi darurat.

🌏 Background

Indonesia merupakan salah satu negara paling rawan bencana di dunia. Ketika bencana terjadi, jaringan komunikasi sering terganggu sehingga masyarakat kesulitan mendapatkan informasi atau meminta bantuan.

TARY hadir sebagai Emergency Assistant berbasis AI yang tetap dapat berfungsi secara offline, sehingga pengguna dapat:

1. memperoleh panduan pertolongan pertama.
2. mengirim pesan darurat.
3. mendapatkan instruksi keselamatan.
4. tetap terhubung dengan orang lain bahkan ketika internet tidak tersedia.

🧠 Teknologi AI & Model (On-Device Inference)

Aplikasi ini ditenagai oleh model AI *Large Language Model* (LLM) yang berjalan sepenuhnya secara *offline* di dalam perangkat pengguna. TARY tidak memerlukan koneksi internet sama sekali saat memproses kueri pengguna di kondisi darurat.
* **Model yang Digunakan:** **Gemma-3-1B-Instruct** (dalam format terkuantisasi `gemma-3-1b-it-q4_0.gguf`) yang diunduh dari Hugging Face.
* **Sistem Inferensi:** Menggunakan *Inference Engine* lokal (`AiChat`) dengan optimalisasi memori (membatasi *chat history* maksimal 3 interaksi terakhir agar performa tetap cepat dan responsif).

📚 Offline Emergency Knowledge Base (Database Darurat)

Untuk memastikan akurasi tingkat tinggi, menghindari halusinasi AI, dan menjamin keselamatan pengguna, TARY mengimplementasikan arsitektur logika **Hybrid**:

1. **SOP Resmi (Bypass AI):** Jika keluhan darurat pengguna memiliki kecocokan (*keyword matching*) dengan database lokal, aplikasi akan langsung memunculkan Standar Operasional Prosedur (SOP) baku secara instan tanpa proses *generate* dari AI.
2. **AI Fallback:** Jika keluhan spesifik tidak ditemukan di database, sistem akan memanggil model Gemma untuk memberikan ringkasan tindakan P3K yang cepat, logis, dan spesifik sesuai konteks cedera.

Data yang terintegrasi di dalam aplikasi (`emergency_data.json`) bersumber dari panduan **PMI (Palang Merah Indonesia)**, **BNPB (Badan Nasional Penanggulangan Bencana)**, dan standar kedaruratan umum. Kategori yang tersedia meliputi:
* **Medis & P3K:** Penanganan perdarahan mayor eksternal, trauma termal (luka bakar), fraktur/patah tulang, dan penanganan tenggelam.
* **Bantuan Hidup Dasar (BHD):** Panduan resusitasi jantung paru (CPR/RJP) dan Heimlich Maneuver untuk korban tersedak.
* **Gigitan & Keracunan:** Evakuasi dan imobilisasi korban gigitan ular berbisa serta penanganan keracunan makanan/bahan kimia.
* **Basic Survival:** Navigasi darurat tanpa kompas (menggunakan bayangan matahari & rasi bintang) saat tersesat.
* **Mitigasi Bencana Alam:** Protokol evakuasi saat terjadi gempa bumi tektonik, erupsi gunung api, banjir bandang, hingga kebakaran struktur gedung.
