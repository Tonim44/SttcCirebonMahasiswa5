package id.ac.sttccirebon.mahasiswa.ui.isikehadiran

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDetailkehadiranBinding
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import org.json.JSONObject

class DetailHadirActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailkehadiranBinding
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
        binding = ActivityDetailkehadiranBinding.inflate(layoutInflater)
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
                Parsing()
            } else {
                noInternetLayout.visibility = View.VISIBLE
                internetLayout.visibility = View.GONE
            }
        }

    private fun Parsing() {

        val loading = LoadingDialog(this)
        loading.startLoading()

        val Matakuliah = binding.detailMatkul
        val Namadosen = binding.detailDosen
        val Tanggal = binding.detailTanggal
        val JamKuliah = binding.detailJam
        val Ruangan = binding.detailRuangan
        val Status = binding.HADIR

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
        Status.text = "Anda dinyatakan hadir pada perkuliahan hari ini"

        val shareLokasi = binding.sharelok
        shareLokasi.setOnClickListener {
            val intent = Intent(this, MapsHadirActivity::class.java)
            startActivity(intent)
        }

        val token = data.getString("token")
        val id_jadwal = absen.idjadwal
        val PhotoAbsen = binding.fotoAbsen
        Log.i("DATA_API", id_jadwal.toString())
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-detail-jadwal-kuliah"
        val params  = RequestParams()
        params.put("token", token)
        Log.i("DATA_API", token.toString())
        params.put("id_detail_jadwal_kuliah", id_jadwal)
        Log.i("DATA_API", id_jadwal.toString())
        client.addHeader("Accept", "application/json")
        Log.i("DATA_SERVER", "Sebelum request")

        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                val responseJson = JSONObject(result)
                val user = responseJson.getJSONObject("result")
                val id_detail_jadwal = user.getJSONObject("detail_jadwal_kuliah")
                val kehadiran = id_detail_jadwal.getJSONObject("kehadiran")
                val fotoAbsen = kehadiran.getString("foto_absensi")
                Log.i("DATA_API", fotoAbsen)
                val Latitude = kehadiran.getString("latitude")
                Log.i("DATA_API", Latitude)
                val Longitude = kehadiran.getString("longitude")
                Log.i("DATA_API", Longitude)

                Picasso.get().load(fotoAbsen).into(PhotoAbsen)

                data.putLatitude(Latitude)
                data.putLongitude(Longitude)

                loading.isDismiss()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable?
            ) {
                Toast.makeText(applicationContext, "Gagal Ditampilkan", Toast.LENGTH_LONG).show()
                loading.isDismiss()
            }
        })
    }
}