package id.ac.sttccirebon.mahasiswa.ui.isikehadiran

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDetailketidakhadiranBinding
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog

class DetailAlpaActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailketidakhadiranBinding
    private lateinit var absen: Absen
    private lateinit var data : DataManager
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView

    companion object {
        const val EXTRA_ABSEN = "extra_absen"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailketidakhadiranBinding.inflate(layoutInflater)

        supportActionBar?.hide()

        internetLayout = binding.InternetLayout
        noInternetLayout = binding.noInternetLayout
        tryAgainButton = binding.tryAgainButton

        drawLayout()

        tryAgainButton.setOnClickListener {
            drawLayout()
        }

        data = DataManager(baseContext)
        val view = binding.root
        setContentView(view)

        val Matakuliah = binding.detailMatkul
        val Namadosen = binding.detailDosen
        val Tanggal = binding.detailTanggal
        val JamKuliah = binding.detailJam
        val Ruangan = binding.detailRuangan
        val Status = binding.ALPA

        val back = binding.back
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        absen = intent.getParcelableExtra<Absen>(IsiKehadiranActvity.EXTRA_ABSEN) as Absen

        val matakuliah = absen.matakuliah
        val namadosen = absen.namadosen
        val tanggal = absen.tanggal
        val jammasuk = absen.jammulai
        val jamselesai = absen.jamselesai
        val ruangan = absen.ruangan

        Matakuliah.text = matakuliah
        Namadosen.text = namadosen
        Tanggal.text = tanggal
        JamKuliah.text = "${jammasuk}-${jamselesai}"
        Ruangan.text = ruangan
        Status.text = "Anda dinyatakan tidak hadir pada perkuliahan hari ini"
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