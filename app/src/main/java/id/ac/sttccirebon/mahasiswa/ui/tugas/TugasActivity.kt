package id.ac.sttccirebon.mahasiswa.ui.tugas

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import id.ac.sttccirebon.mahasiswa.R
import io.grpc.Deadline

class TugasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_tugas)

        val TittleTugas = findViewById<TextView>(R.id.nama_tugas)
        val NamaMatkul = findViewById<TextView>(R.id.nama_matkul)
        val TanggalDibuat = findViewById<TextView>(R.id.dibuat_pada)
        val NamaDosen = findViewById<TextView>(R.id.nama_dosen)
        val Deadline = findViewById<TextView>(R.id.tanggal_deadline)

        val bundle : Bundle?= intent.extras
        val tittle_tugas = bundle!!.getString("tittle_tugas")
        val mata_kuliah = bundle!!.getString("mata_kuliah")
        val tanggal_dibuat = bundle!!.getString("tanggal_dibuat")
        val nama_dosen = bundle!!.getString("nama_dosen")
        val deadline = bundle!!.getString("deadline")

        TittleTugas.text = tittle_tugas
        NamaMatkul.text = mata_kuliah
        TanggalDibuat.text = tanggal_dibuat
        NamaDosen.text = nama_dosen
        Deadline.text = deadline

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

    }
}