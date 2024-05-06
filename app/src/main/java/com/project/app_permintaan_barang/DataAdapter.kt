package com.project.app_permintaan_barang

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.app_permintaan_barang.model.DataItem
import java.text.SimpleDateFormat
import java.util.*

class DataAdapter(private val dataList: List<DataItem>) : RecyclerView.Adapter<DataAdapter.DataViewHolder>() {

    // Inisialisasi data yang telah diurutkan dari yang terbaru
    private val sortedDataList = dataList.sortedByDescending { it.createdDate }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = sortedDataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = sortedDataList.size


    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDataIDPermintaan: TextView = itemView.findViewById(R.id.textViewDataIDPermintaan)
        private val textViewDataNamaBarang: TextView = itemView.findViewById(R.id.textViewDataNamaBarang)
        private val textViewDataDeskripsi: TextView = itemView.findViewById(R.id.textViewDataDeskripsi)
        private val textViewDataJumlah: TextView = itemView.findViewById(R.id.textViewDataJumlah)
        private val textViewDataCatatanAdmin: TextView = itemView.findViewById(R.id.textViewDataCatatanAdmin)
        private val textViewDataStatus: TextView = itemView.findViewById(R.id.textViewDataStatus)
        private val textViewDataTanggalPermintaan: TextView = itemView.findViewById(R.id.textViewDataTanggalPermintaan)
        private val textViewDataUsername: TextView = itemView.findViewById(R.id.textViewDataUsername)
        private val textViewDataFilePermintaan: TextView = itemView.findViewById(R.id.textViewDataFilePermintaan)

        fun bind(dataItem: DataItem) {

            // Mengubah format tanggal
            val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .parse(dataItem.tanggalPermintaan.joinToString("-"))

            textViewDataIDPermintaan.text = dataItem.idPermintaanBarang.toString()
            textViewDataNamaBarang.text = dataItem.namaBarang
            textViewDataDeskripsi.text = dataItem.deskripsiBarang
            textViewDataJumlah.text = dataItem.jumlahPermintaan.toString()

            val catatanAdmin = dataItem.catatanAdmin.ifEmpty { "-" }
            textViewDataCatatanAdmin.text = catatanAdmin

            textViewDataStatus.text = dataItem.statusPermintaan
            textViewDataTanggalPermintaan.text = date?.let { dateFormatter.format(it) }
            textViewDataUsername.text = dataItem.user.username
            textViewDataFilePermintaan.text = dataItem.filePermintaan
        }
    }
}
