package com.sanomush.tari

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

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Mengarahkan class ini ke layout fragment_chat.xml yang baru dibuat
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Binding view secara manual (tanpa ViewBinding dulu biar cepat test UI)
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

        btnSend.setOnClickListener {
            val query = etInput.text.toString()
            if (query.isNotEmpty()) {
                Toast.makeText(requireContext(), "Mengirim: $query", Toast.LENGTH_SHORT).show()
                etInput.text.clear()
            }
        }
    }
}