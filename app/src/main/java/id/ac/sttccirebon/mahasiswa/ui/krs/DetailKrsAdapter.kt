package id.ac.sttccirebon.mahasiswa.ui.krs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.mahasiswa.R

class DetailKrsAdapter(val krsDetail: ArrayList<DetailKrs>) : RecyclerView.Adapter<DetailKrsAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_krstable, parent, false)
        return ListViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val krs = krsDetail[position]
        holder.MataKuliah.text = krs.matakuliah.toString()
        holder.Dosen.text = krs.dosen.toString()
        holder.Sks.text = "   ${krs.jumlah_sks.toString()}  "
    }

    override fun getItemCount(): Int {
        return krsDetail.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val MataKuliah = itemView.findViewById<TextView>(R.id.table_matakuliah)
        val Dosen = itemView.findViewById<TextView>(R.id.table_dosen)
        val Sks = itemView.findViewById<TextView>(R.id.table_sks)
    }

}
