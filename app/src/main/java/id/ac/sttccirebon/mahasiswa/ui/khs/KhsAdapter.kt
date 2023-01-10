package id.ac.sttccirebon.mahasiswa.ui.khs

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.R

class KhsAdapter(val khsList: ArrayList<Khs>) : RecyclerView.Adapter<KhsAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_khs, parent, false)
        return ListViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val khs = khsList[position]
        holder.Semester.text = "Semester ${khs.semester.toString()}"
        holder.TahunAjaran.text = khs.tahun_ajaran.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, TableKhsActivity::class.java)
            intent.putExtra(TableKhsActivity.EXTRA_TABLEKHS, khs)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return khsList.size
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Semester = itemView.findViewById<TextView>(R.id.semester_khs)
        val TahunAjaran = itemView.findViewById<TextView>(R.id.tahunajaran)
    }

}