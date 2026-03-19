package com.sanomush.tari

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanomush.tari.adapter.BookAdapter
import com.sanomush.tari.helper.JsonFallbackHelper

class BookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        val rvBook: RecyclerView = findViewById(R.id.rvBook)
        rvBook.layoutManager = LinearLayoutManager(this)

        // 1. Panggil Helper untuk baca JSON
        val jsonHelper = JsonFallbackHelper(this)
        val dataDarurat = jsonHelper.loadEmergencyData()

        // 2. Masukkan data ke Adapter
        val adapter = BookAdapter(dataDarurat)
        rvBook.adapter = adapter
    }
}