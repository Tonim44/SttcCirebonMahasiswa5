package id.ac.sttccirebon.mahasiswa.ui.tugas

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.mahasiswa.R

class TugasAdapter(
    private val context: TugasFragment, private val tugasList: List<Tugas>) : RecyclerView.Adapter<TugasAdapter.ViewHolder>() {

    var onItemClik : ((Tugas)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_tugas, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: TugasAdapter.ViewHolder, position: Int) {
        onItemClik?.let { holder.bindView(tugasList[position], it) }
        val tugas = tugasList[position]
        holder.textViewTittle.text=tugas.tittle_tugas
        holder.textViewMatkul.text=tugas.mata_kuliah
        holder.textViewTanggalDibuat.text=tugas.tanggal_dibuat
        holder.NamaDosen.equals(tugas.nama_dosen)
        holder.Deadline.equals(tugas.deadline)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, TugasActivity::class.java)
            intent.putExtra("tittle_tugas",tugas.tittle_tugas)
            intent.putExtra("mata_kuliah",tugas.mata_kuliah)
            intent.putExtra("tanggal_dibuat",tugas.tanggal_dibuat)
            intent.putExtra("nama_dosen", tugas.nama_dosen)
            intent.putExtra("deadline", tugas.deadline)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = tugasList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTittle = itemView.findViewById<TextView>(R.id.tittle_tugas)
        val textViewMatkul = itemView.findViewById<TextView>(R.id.matakul_tugas)
        val textViewTanggalDibuat = itemView.findViewById<TextView>(R.id.tanggal_dibuat)
        val NamaDosen = String
        val Deadline = String

        fun bindView(tugas: Tugas, listner: (Tugas) -> Unit) {
            textViewTittle.text = tugas.tittle_tugas
            textViewMatkul.text = tugas.mata_kuliah
            textViewTanggalDibuat.text = tugas.tanggal_dibuat
            NamaDosen.equals(tugas.nama_dosen)
            Deadline.equals(tugas.deadline)
        }
    }

}