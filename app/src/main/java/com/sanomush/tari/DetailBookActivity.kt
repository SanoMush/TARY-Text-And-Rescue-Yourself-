package com.sanomush.tari

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_book)

        // Menangkap data yang dikirim dari Adapter
        val judul = intent.getStringExtra("EXTRA_JUDUL") ?: "-"
        val kategori = intent.getStringExtra("EXTRA_KATEGORI") ?: "-"
        val sumber = intent.getStringExtra("EXTRA_SUMBER") ?: "-"

        // Menangkap data array list
        val listTindakan = intent.getStringArrayListExtra("EXTRA_TINDAKAN") ?: arrayListOf()
        val listLarangan = intent.getStringArrayListExtra("EXTRA_LARANGAN") ?: arrayListOf()
        val listPerlengkapan = intent.getStringArrayListExtra("EXTRA_PERLENGKAPAN") ?: arrayListOf()

        // Bind ke View
        findViewById<TextView>(R.id.tvDetailJudul).text = judul
        findViewById<TextView>(R.id.tvDetailKategori).text = kategori
        findViewById<TextView>(R.id.tvDetailSumber).text = "Sumber: $sumber"

        // Fungsi bantuan untuk mengubah List menjadi string dengan bullet points
        fun formatToBulletPoints(list: List<String>): String {
            if (list.isEmpty()) return "Tidak ada data."
            return list.joinToString(separator = "\n\n") { "• $it" }
        }

        findViewById<TextView>(R.id.tvDetailTindakan).text = formatToBulletPoints(listTindakan)
        findViewById<TextView>(R.id.tvDetailLarangan).text = formatToBulletPoints(listLarangan)
        findViewById<TextView>(R.id.tvDetailPerlengkapan).text = formatToBulletPoints(listPerlengkapan)
    }
}