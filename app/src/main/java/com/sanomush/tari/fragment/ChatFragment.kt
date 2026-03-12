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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanomush.tari.R
import com.sanomush.tari.adapter.ChatAdapter
import com.sanomush.tari.data.ChatMessage
import com.sanomush.tari.helper.JsonFallbackHelper

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        JsonFallbackHelper.initDatabase(requireContext())

        val btnSos = view.findViewById<Button>(R.id.btnSos)
        val btnFlashlight = view.findViewById<ImageButton>(R.id.btnFlashlight)
        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val etInput = view.findViewById<EditText>(R.id.etInput)
        val rvChat = view.findViewById<RecyclerView>(R.id.rvChat)

        // 1. SETUP RECYCLER VIEW & ADAPTER
        chatAdapter = ChatAdapter(chatList)
        rvChat.layoutManager = LinearLayoutManager(requireContext())
        rvChat.adapter = chatAdapter

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
                // Tambah gelembung chat User (Kanan)
                chatAdapter.addMessage(ChatMessage(query, isUser = true))
                rvChat.scrollToPosition(chatList.size - 1) // Auto-scroll ke bawah

                // Bersihkan input
                etInput.text.clear()

                // Cari jawaban di JSON
                val taryResponse = JsonFallbackHelper.searchInstruction(query)

                // Tambah gelembung chat TARY (Kiri)
                chatAdapter.addMessage(ChatMessage(taryResponse, isUser = false))
                rvChat.scrollToPosition(chatList.size - 1) // Auto-scroll ke bawah
            }
        }
    }
}