package com.sanomush.tari

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sanomush.tari.fragment.ChatFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle padding untuk EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Langsung arahkan ke ChatFragment (F-01: Zero-login screen)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                // Nanti kita buat file fragment_chat.xml dan class ChatFragment
                .replace(R.id.main, ChatFragment())
                .commit()
        }
    }
}