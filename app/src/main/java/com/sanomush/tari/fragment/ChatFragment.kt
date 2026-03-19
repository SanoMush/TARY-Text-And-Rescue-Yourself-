package com.sanomush.tari.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanomush.tari.BookActivity
import com.sanomush.tari.R
import com.sanomush.tari.adapter.ChatAdapter
import com.sanomush.tari.data.ChatMessage
import com.sanomush.tari.helper.CompassHelper
import com.sanomush.tari.helper.CompassView
import com.sanomush.tari.helper.HardwareUtils
import com.sanomush.tari.helper.LocationUtils
import com.sanomush.tari.helper.LlmHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()

    // Mesin AI TARY
    private lateinit var taryAi: LlmHelper

    // Kompas & GPS
    private lateinit var compassHelper: CompassHelper
    private var isCompassVisible = false
    private lateinit var locationManager: LocationManager
    private var locationListener: LocationListener? = null

    // Views
    private lateinit var compassPanel: LinearLayout
    private lateinit var compassView: CompassView
    private lateinit var btnCompass: ImageButton
    private lateinit var tvDegrees: TextView
    private lateinit var tvDirection: TextView
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var tvCompassStatus: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            showTriageDialog()
            startLocationUpdates()
        } else {
            Toast.makeText(requireContext(), "Izin lokasi ditolak.", Toast.LENGTH_SHORT).show()
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


        taryAi = LlmHelper(requireContext())
        val btnSos = view.findViewById<Button>(R.id.btnSos)
        val btnFlashlight = view.findViewById<ImageButton>(R.id.btnFlashlight)
        val btnBukuSaku = view.findViewById<ImageButton>(R.id.btnBukuSaku) // Tombol Buku Saku


        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val etInput = view.findViewById<EditText>(R.id.etInput)
        val rvChat = view.findViewById<RecyclerView>(R.id.rvChat)

        btnSend.isEnabled = false
        etInput.isEnabled = false
        etInput.hint = "Mengekstrak mesin TARY (Bisa 2-3 Menit)..."

        viewLifecycleOwner.lifecycleScope.launch {
            taryAi.loadModel()

            withContext(kotlinx.coroutines.Dispatchers.Main) {
                btnSend.isEnabled = true
                etInput.isEnabled = true
                etInput.hint = "Ketik keluhan darurat di sini..."
                Toast.makeText(requireContext(), "TARY Siap Digunakan!", Toast.LENGTH_LONG).show()
            }
        }

        btnCompass = view.findViewById(R.id.btnCompass)
        compassPanel = view.findViewById(R.id.compassPanel)
        compassView = view.findViewById(R.id.compassView)
        tvDegrees = view.findViewById(R.id.tvDegrees)
        tvDirection = view.findViewById(R.id.tvDirection)
        tvLatitude = view.findViewById(R.id.tvLatitude)
        tvLongitude = view.findViewById(R.id.tvLongitude)
        tvAccuracy = view.findViewById(R.id.tvAccuracy)
        tvCompassStatus = view.findViewById(R.id.tvCompassStatus)

        chatAdapter = ChatAdapter(chatList)
        rvChat.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = false
        }
        rvChat.adapter = chatAdapter

        compassHelper = CompassHelper(requireContext())
        if (!compassHelper.isAvailable()) {
            tvCompassStatus.text = "⚠️ Sensor kompas tidak tersedia di perangkat ini"
        }
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnFlashlight.setOnClickListener {
            val isOn = HardwareUtils.toggleFlashlight(requireContext())
            if (isOn) {
                btnFlashlight.setColorFilter(android.graphics.Color.parseColor("#FFEB3B"))
                Toast.makeText(requireContext(), "Senter Menyala", Toast.LENGTH_SHORT).show()
            } else {
                btnFlashlight.clearColorFilter()
                Toast.makeText(requireContext(), "Senter Mati", Toast.LENGTH_SHORT).show()
            }
        }


        btnBukuSaku.setOnClickListener {
            val intent = Intent(requireContext(), BookActivity::class.java)
            startActivity(intent)
        }


        btnCompass.setOnClickListener { toggleCompass() }
        btnSos.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                showTriageDialog()
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        }

        btnSend.setOnClickListener {
            val query = etInput.text.toString()
            if (query.isNotEmpty()) {
                chatList.add(ChatMessage(query, isUser = true))
                chatAdapter.notifyItemInserted(chatList.size - 1)
                rvChat.scrollToPosition(chatList.size - 1)
                etInput.text.clear()

                btnSend.isEnabled = false

                val aiMessageIndex = chatList.size
                chatList.add(ChatMessage("", isUser = false))
                chatAdapter.notifyItemInserted(aiMessageIndex)
                rvChat.scrollToPosition(aiMessageIndex)

                val aiResponseBuilder = StringBuilder()

                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                    taryAi.generateResponse(query).collect { token ->
                        aiResponseBuilder.append(token)

                        withContext(Dispatchers.Main) {
                            chatList[aiMessageIndex] = ChatMessage(aiResponseBuilder.toString(), isUser = false)
                            chatAdapter.notifyItemChanged(aiMessageIndex)
                            rvChat.scrollToPosition(aiMessageIndex)
                        }
                    }

                    // ── TAMBAHAN BARU: SIMPAN INGATAN SETELAH AI SELESAI NGETIK ──
                    val finalAiResponse = aiResponseBuilder.toString().trim()
                    taryAi.addChatHistory(query, finalAiResponse)
                    // ─────────────────────────────────────────────────────────────

                    withContext(Dispatchers.Main) {
                        btnSend.isEnabled = true
                    }
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        }
    }

    private fun toggleCompass() {
        isCompassVisible = !isCompassVisible
        if (isCompassVisible) {
            compassPanel.visibility = View.VISIBLE
            btnCompass.setColorFilter(
                android.graphics.Color.parseColor("#FFEB3B"),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            compassHelper.start(object : CompassHelper.CompassListener {
                override fun onAzimuthChanged(azimuth: Float, direction: String, degrees: Int) {
                    activity?.runOnUiThread {
                        compassView.setAzimuth(azimuth)
                        tvDegrees.text = "$degrees°"
                        tvDirection.text = direction
                        tvCompassStatus.text = "✅ Sensor aktif — pegang HP sejajar tanah"
                    }
                }
            })
        } else {
            compassPanel.visibility = View.GONE
            compassHelper.stop()
            btnCompass.setColorFilter(
                android.graphics.Color.WHITE,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                updateLocationUI(location)
            }
            @Deprecated("Deprecated in API 29")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        locationListener = listener

        try { locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f, listener) } catch (_: Exception) {}
        try { locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 5f, listener) } catch (_: Exception) {}

        val lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnown?.let { updateLocationUI(it) }
    }

    private fun updateLocationUI(location: Location) {
        val lat = String.format("%.6f", location.latitude)
        val lon = String.format("%.6f", location.longitude)
        val acc = String.format("%.1f", location.accuracy)
        tvLatitude.text = "Lat: $lat"
        tvLongitude.text = "Long: $lon"
        tvAccuracy.text = "Akurasi: ±${acc}m"
    }

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
            val sosMessage = "SOS|TARY|Loc:$lat,$lon|Cond:$status"

            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("TARY SOS", sosMessage)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Pesan disalin ke Clipboard!", Toast.LENGTH_SHORT).show()

            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:")
                putExtra("sms_body", sosMessage)
            }
            startActivity(smsIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isCompassVisible) compassHelper.stop()
    }

    override fun onResume() {
        super.onResume()
        if (isCompassVisible) {
            compassHelper.start(object : CompassHelper.CompassListener {
                override fun onAzimuthChanged(azimuth: Float, direction: String, degrees: Int) {
                    activity?.runOnUiThread {
                        compassView.setAzimuth(azimuth)
                        tvDegrees.text = "$degrees°"
                        tvDirection.text = direction
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compassHelper.stop()
        locationListener?.let { locationManager.removeUpdates(it) }

        // ── MATIKAN MESIN AI BIAR RAM GAK BOCOR ──
        taryAi.unloadModel()
    }
}