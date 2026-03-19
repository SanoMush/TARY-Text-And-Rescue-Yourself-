package com.sanomush.tari.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sanomush.tari.R
import com.sanomush.tari.data.EmergencyData

class BookAdapter(private val listData: List<EmergencyData>) :
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
    }

    override fun getItemCount(): Int = listData.size
}