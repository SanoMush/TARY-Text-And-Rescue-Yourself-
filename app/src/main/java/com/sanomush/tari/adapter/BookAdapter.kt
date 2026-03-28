package com.sanomush.tari.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sanomush.tari.DetailBookActivity
import com.sanomush.tari.R
import com.sanomush.tari.data.EmergencyData

// PERHATIKAN: 'val' diubah menjadi 'var' agar datanya bisa di-update
class BookAdapter(private var listData: List<EmergencyData>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKategori: TextView = itemView.findViewById(R.id.tvKategori)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvSumber: TextView = itemView.findViewById(R.id.tvSumber)
        val tvPreview: TextView = itemView.findViewById(R.id.tvPreviewTindakan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val data = listData[position]
        holder.tvKategori.text = data.kategori
        holder.tvJudul.text = data.judul
        holder.tvSumber.text = "Sumber: ${data.sumber}"

        if (data.tindakan.isNotEmpty()) {
            holder.tvPreview.text = data.tindakan[0]
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailBookActivity::class.java).apply {
                putExtra("EXTRA_JUDUL", data.judul)
                putExtra("EXTRA_KATEGORI", data.kategori)
                putExtra("EXTRA_SUMBER", data.sumber)

                putStringArrayListExtra("EXTRA_TINDAKAN", ArrayList(data.tindakan))
                putStringArrayListExtra("EXTRA_LARANGAN", ArrayList(data.larangan))
                putStringArrayListExtra("EXTRA_PERLENGKAPAN", ArrayList(data.perlengkapan))
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listData.size

    // --- FUNGSI BARU UNTUK SEARCH BAR ---
    fun updateData(newList: List<EmergencyData>) {
        listData = newList
        notifyDataSetChanged() // Meminta RecyclerView merender ulang layar
    }
}