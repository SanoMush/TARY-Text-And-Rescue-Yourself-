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
import com.sanomush.tari.helper.LocationUtils
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.app.AlertDialog

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            showTriageDialog()
        } else {
            Toast.makeText(requireContext(), "Izin lokasi ditolak, gagal membuat koordinat presisi.", Toast.LENGTH_SHORT).show()
        }
    }

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
            // Cek apakah udah dikasih izin lokasi
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                showTriageDialog()
            } else {
                // Kalau belum, minta izin ke user
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
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
    } // <-- TUTUP KURUNG onViewCreated DI SINI

    // KEDUA FUNGSI INI HARUS BERADA DI LUAR onViewCreated
    private fun showTriageDialog() {
        val conditions = arrayOf("Aman", "Luka Ringan", "Kritis")
        AlertDialog.Builder(requireContext())
            .setTitle("Kondisi Anda Saat Ini?")
            .setItems(conditions) { _, which ->
                val status = conditions[which]
                generateSosMessage(status)
            }
            .show()
    }

    private fun generateSosMessage(status: String) {
        Toast.makeText(requireContext(), "Mengambil lokasi...", Toast.LENGTH_SHORT).show()

        LocationUtils.getLastKnownLocation(requireContext()) { location ->
            val lat = location?.latitude?.toString() ?: "Unknown"
            val lon = location?.longitude?.toString() ?: "Unknown"

            // Format string sesuai F-03 Matriks
            val sosMessage = "SOS|TARY|Loc:$lat,$lon|Cond:$status"

            // Salin ke Clipboard
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("TARY SOS", sosMessage)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Pesan disalin ke Clipboard!", Toast.LENGTH_SHORT).show()

            // Buka aplikasi SMS (Intent.ACTION_SENDTO)
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:") // Spesifik untuk aplikasi SMS
                putExtra("sms_body", sosMessage)
            }
            startActivity(smsIntent)
        }
    }
}