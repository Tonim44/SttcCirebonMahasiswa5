package id.ac.sttccirebon.mahasiswa.ui.krs

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.ui.khs.DetailKhsActivity

class KrsAdapter(val krsList: ArrayList<Krs>) : RecyclerView.Adapter<KrsAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_krs, parent, false)
        return ListViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val krs = krsList[position]
        holder.Semester.text = "Semester ${krs.semester.toString()}"
        holder.TahunAjaran.text = krs.tahun_ajaran.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailKrsActivity::class.java)
            intent.putExtra(DetailKrsActivity.EXTRA_KRS, krs)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return krsList.size
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Semester = itemView.findViewById<TextView>(R.id.semester_krs)
        val TahunAjaran = itemView.findViewById<TextView>(R.id.tahunajaran)
    }

}