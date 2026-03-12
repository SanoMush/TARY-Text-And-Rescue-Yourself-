package com.sanomush.tari.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sanomush.tari.R
import com.sanomush.tari.helper.JsonFallbackHelper

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. INISIALISASI DATABASE JSON DI SINI
        JsonFallbackHelper.initDatabase(requireContext())

        val btnSos = view.findViewById<Button>(R.id.btnSos)
        val btnFlashlight = view.findViewById<ImageButton>(R.id.btnFlashlight)
        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val etInput = view.findViewById<EditText>(R.id.etInput)
        val rvChat = view.findViewById<RecyclerView>(R.id.rvChat)

        btnSos.setOnClickListener {
            Toast.makeText(requireContext(), "F-03: SOS Beacon Triggered!", Toast.LENGTH_SHORT).show()
        }

        btnFlashlight.setOnClickListener {
            Toast.makeText(requireContext(), "F-08: Flashlight Toggled!", Toast.LENGTH_SHORT).show()
        }

        // 2. UBAH LOGIKA TOMBOL KIRIM
        btnSend.setOnClickListener {
            val query = etInput.text.toString()
            if (query.isNotEmpty()) {
                // Tampilkan pesan user sementara
                Toast.makeText(requireContext(), "Kamu: $query", Toast.LENGTH_SHORT).show()

                // Cari jawaban di JSON berdasarkan input user
                val aiResponse = JsonFallbackHelper.searchInstruction(query)

                // Tampilkan jawaban AI/JSON (pakai Toast panjang dulu untuk ngetes)
                Toast.makeText(requireContext(), "TARY: $aiResponse", Toast.LENGTH_LONG).show()

                etInput.text.clear()
            }
        }
    }
}