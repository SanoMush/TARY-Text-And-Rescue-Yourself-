package com.sanomush.tari

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanomush.tari.adapter.BookAdapter
import com.sanomush.tari.data.EmergencyData
import com.sanomush.tari.helper.JsonFallbackHelper

class BookActivity : AppCompatActivity() {

    private lateinit var adapter: BookAdapter
    private var originalData: List<EmergencyData> = listOf() // Menyimpan data asli

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        // Menghitamkan Status Bar atas
        window.statusBarColor = android.graphics.Color.BLACK

        // Fungsi klik tombol back
        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val rvBook: RecyclerView = findViewById(R.id.rvBook)
        rvBook.layoutManager = LinearLayoutManager(this)

        // Panggil Helper untuk baca JSON
        val jsonHelper = JsonFallbackHelper(this)
        originalData = jsonHelper.loadEmergencyData() // Simpan ke variabel penampung

        // Set Adapter awal
        adapter = BookAdapter(originalData)
        rvBook.adapter = adapter

        // --- LOGIKA SEARCH BAR ---
        val etSearch = findViewById<EditText>(R.id.etSearchBook)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            // Fungsi ini dipanggil setiap kali user mengetik atau menghapus huruf
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                filterData(query)
            }
        })
    }

    // Fungsi untuk menyaring data berdasarkan input user
    private fun filterData(query: String) {
        if (query.isEmpty()) {
            // Jika kolom kosong, kembalikan semua data asli
            adapter.updateData(originalData)
        } else {
            // Jika ada teks, saring berdasarkan judul yang mengandung teks tersebut (mengabaikan huruf besar/kecil)
            val filteredList = originalData.filter { data ->
                data.judul.contains(query, ignoreCase = true) ||
                        data.kategori.contains(query, ignoreCase = true)
            }
            // Kirim data hasil saringan ke adapter
            adapter.updateData(filteredList)
        }
    }
}