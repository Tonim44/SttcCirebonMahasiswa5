package id.ac.sttccirebon.mahasiswa

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDashboardBinding
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDashboardBinding.inflate
import id.ac.sttccirebon.mahasiswa.databinding.FragmentIsikehadiranBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.ui.isikehadiran.Absen
import id.ac.sttccirebon.mahasiswa.ui.isikehadiran.AbsenAdapter
import id.ac.sttccirebon.mahasiswa.ui.isikehadiran.IsiKehadiranFragment
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ListAbsenActivity : AppCompatActivity() {

    private lateinit var binding: FragmentIsikehadiranBinding
    private lateinit  var  data : DataManager
    private var absenList = arrayListOf<Absen>()
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = FragmentIsikehadiranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        internetLayout = binding.InternetLayout
        noInternetLayout = binding.noInternetLayout
        tryAgainButton = binding.tryAgainButton

        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                drawLayout()
                loading.isDismiss()
            }
        }, 2000)

        tryAgainButton.setOnClickListener {
            val loading = LoadingDialog(this)
            loading.startLoading()
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    drawLayout()
                    loading.isDismiss()
                }
            }, 2000)
        }
    }

    @SuppressLint("NewApi")
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))

    }

    private fun drawLayout() {

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

        data = DataManager(baseContext)
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-jadwal-kuliah"
        val params  = RequestParams()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDateandTime: String = sdf.format(Date())
        val token = data.getString("token")
        params.put("token", token)
        android.util.Log.i("DATA_API", token.toString())
        params.put("date", currentDateandTime)
        android.util.Log.i("DATA_API", currentDateandTime.toString())
        client.addHeader("Accept", "application/json")

        val Tanggal : TextView = findViewById(R.id.present_date)
        Tanggal.text = currentDateandTime

        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<cz.msebera.android.httpclient.Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                val responseJson = JSONObject(result)
                val Result = responseJson.getJSONObject("result")
                val jadwalKuliah = Result.getJSONArray("jadwal_kuliah")
                absenList.clear()

                for(i in 0 until jadwalKuliah.length()) {
                    val jadwalMatkul = jadwalKuliah.getJSONObject(i)
                    val listMatkul = Absen(
                        jadwalMatkul.getInt("id_detail_jadwal_kuliah"),
                        jadwalMatkul.getString("mata_kuliah"),
                        jadwalMatkul.getString("nama_dosen"),
                        jadwalMatkul.getString("nama_assisten"),
                        jadwalMatkul.getString("tanggal"),
                        jadwalMatkul.getString("jam_mulai"),
                        jadwalMatkul.getString("jam_selesai"),
                        jadwalMatkul.getString("ruangan"),
                        jadwalMatkul.getString("status")

                    )
                    absenList.add(listMatkul)
                }
                android.util.Log.i("DATA_API", jadwalKuliah.toString())
                showAbsenList()
                loading.isDismiss()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<cz.msebera.android.httpclient.Header>,
                responseBody: ByteArray,
                error: Throwable?
            ) {
                android.util.Log.i("DATA_API", "GAGAL DITAMPILKAN")
                loading.isDismiss()
            }
        })
    }

    private fun showAbsenList() {
        val recyclerView : RecyclerView = findViewById(R.id.jadwal_perkuliahan)
        recyclerView.layoutManager = LinearLayoutManager(this@ListAbsenActivity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = AbsenAdapter(absenList)
    }

    override fun onBackPressed() {
        val intent = Intent(this@ListAbsenActivity, DashboardActivity::class.java)
        startActivity(intent)
    }

}