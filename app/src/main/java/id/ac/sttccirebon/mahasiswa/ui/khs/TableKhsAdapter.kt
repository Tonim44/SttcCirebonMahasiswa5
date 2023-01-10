package id.ac.sttccirebon.mahasiswa.ui.khs

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.ui.krs.DetailKrs
import id.ac.sttccirebon.mahasiswa.ui.krs.DetailKrsActivity
import id.ac.sttccirebon.mahasiswa.ui.krs.DetailKrsAdapter

class TableKhsAdapter(val khsTable: ArrayList<TableKHS>) : RecyclerView.Adapter<TableKhsAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_khstable, parent, false)
        return ListViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val khs = khsTable[position]
        holder.MataKuliah.text = khs.matakuliah.toString()
        holder.Dosen.text = khs.dosen.toString()

        if (khs.nilai_huruf.equals("null")){
            holder.Nilai.text = "  -  "
        }else{
            holder.Nilai.text = "   ${khs.nilai_huruf}  "
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailKhsActivity::class.java)
            intent.putExtra(DetailKhsActivity.EXTRA_KHS, khs)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return khsTable.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val MataKuliah = itemView.findViewById<TextView>(R.id.table_matakuliah)
        val Dosen = itemView.findViewById<TextView>(R.id.table_dosen)
        val Nilai = itemView.findViewById<TextView>(R.id.table_nilaimutlak)
    }

}
