package id.ac.sttccirebon.mahasiswa.ui.pengumuman

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDetailkhsBinding
import id.ac.sttccirebon.mahasiswa.databinding.ActivityPengumumanBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.khs.DetailKhsActivity
import id.ac.sttccirebon.mahasiswa.ui.khs.Khs
import id.ac.sttccirebon.mahasiswa.ui.khs.TableKHS
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog

class PengumumanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengumumanBinding
    private lateinit var pengumuman: Pengumuman
    private lateinit var data : DataManager
    private lateinit var dokumen: CardView
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView


    companion object {
        const val EXTRA_PENGUMUMAN = "extra_pengumuman"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityPengumumanBinding.inflate(layoutInflater)
        data = DataManager(baseContext)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        internetLayout = binding.InternetLayout
        noInternetLayout = binding.noInternetLayout
        tryAgainButton = binding.tryAgainButton

        drawLayout()

        tryAgainButton.setOnClickListener {
            drawLayout()
        }

        dokumen = binding.lihatDokumen

        val Kegiatan = binding.pengumuman
        val TanggalPengumuman = binding.tanggalPengumuman
        val Isi = binding.isi

        pengumuman = intent.getParcelableExtra<Pengumuman>(PengumumanActivity.EXTRA_PENGUMUMAN) as Pengumuman

        val kegiatan = pengumuman.judul
        val tanggal_pengumuman = pengumuman.tanggal
        val isi = pengumuman.konten
        val file = pengumuman.file

        if (file.equals("null")) {
            dokumen.visibility = View.GONE
        }

        Kegiatan.text = kegiatan
        TanggalPengumuman.text = tanggal_pengumuman
        Isi.text = isi

       dokumen.setOnClickListener{
          val openURL = Intent(android.content.Intent.ACTION_VIEW)
                openURL.data = Uri.parse(pengumuman.file)
                startActivity(openURL)
        }

        binding.back.setOnClickListener(View.OnClickListener { onBackPressed() })

    }

    @SuppressLint("NewApi")
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
    }

    private fun drawLayout() {

        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                loading.isDismiss()
            }
        }, 2000)

        if (isNetworkAvailable()) {
            internetLayout.visibility = View.VISIBLE
            noInternetLayout.visibility = View.GONE
        } else {
            noInternetLayout.visibility = View.VISIBLE
            internetLayout.visibility = View.GONE
        }
    }
}