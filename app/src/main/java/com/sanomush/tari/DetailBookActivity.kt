package com.sanomush.tari

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) { // <-- INI ONCREATE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_book)

        window.statusBarColor = android.graphics.Color.BLACK

        findViewById<TextView>(R.id.btnBackDetail).setOnClickListener {
            finish()
        }



        val judul = intent.getStringExtra("EXTRA_JUDUL") ?: "-"
        val kategori = intent.getStringExtra("EXTRA_KATEGORI") ?: "-"
        val sumber = intent.getStringExtra("EXTRA_SUMBER") ?: "-"


        val listTindakan = intent.getStringArrayListExtra("EXTRA_TINDAKAN") ?: arrayListOf()
        val listLarangan = intent.getStringArrayListExtra("EXTRA_LARANGAN") ?: arrayListOf()
        val listPerlengkapan = intent.getStringArrayListExtra("EXTRA_PERLENGKAPAN") ?: arrayListOf()


        findViewById<TextView>(R.id.tvDetailJudul).text = judul
        findViewById<TextView>(R.id.tvDetailKategori).text = kategori
        findViewById<TextView>(R.id.tvDetailSumber).text = "Sumber: $sumber"

        fun formatToBulletPoints(list: List<String>): String {
            if (list.isEmpty()) return "Tidak ada data."
            return list.joinToString(separator = "\n\n") { "• $it" }
        }

        findViewById<TextView>(R.id.tvDetailTindakan).text = formatToBulletPoints(listTindakan)
        findViewById<TextView>(R.id.tvDetailLarangan).text = formatToBulletPoints(listLarangan)
        findViewById<TextView>(R.id.tvDetailPerlengkapan).text = formatToBulletPoints(listPerlengkapan)
    }
}