package id.ac.sttccirebon.mahasiswa.ui.pengumuman

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.ui.khs.TableKhsActivity

class PengumumanAdapter(val pengumumanList: List<Pengumuman>) : RecyclerView.Adapter<PengumumanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_pengumuman, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pengumuman = pengumumanList[position]
        holder.textViewKegiatan.text=pengumuman.judul
        holder.textViewTanggalPengumuman.text=pengumuman.tanggal


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PengumumanActivity::class.java)
            intent.putExtra(PengumumanActivity.EXTRA_PENGUMUMAN, pengumuman)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = pengumumanList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewKegiatan = itemView.findViewById<TextView>(R.id.tittle_pengumuman)
        val textViewTanggalPengumuman = itemView.findViewById<TextView>(R.id.tanggal_pengumuman)

    }

}