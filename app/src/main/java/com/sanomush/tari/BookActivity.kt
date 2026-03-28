package com.sanomush.tari

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanomush.tari.adapter.BookAdapter
import com.sanomush.tari.helper.JsonFallbackHelper

class BookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) { // <-- INI ONCREATE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        window.statusBarColor = android.graphics.Color.BLACK

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val rvBook: RecyclerView = findViewById(R.id.rvBook)
        rvBook.layoutManager = LinearLayoutManager(this)
        val jsonHelper = JsonFallbackHelper(this)
        val dataDarurat = jsonHelper.loadEmergencyData()
        val adapter = BookAdapter(dataDarurat)
        rvBook.adapter = adapter
    }
}