package id.ac.sttccirebon.mahasiswa.ui.khs

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDetailtablekhsBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDetailtablekrsBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import org.json.JSONObject

class TableKhsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailtablekhsBinding
    private lateinit var khs: Khs
    private lateinit var data : DataManager
    private var khsTable = arrayListOf<TableKHS>()
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView

    companion object {
        const val EXTRA_TABLEKHS = "extra_tablekhs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailtablekhsBinding.inflate(layoutInflater)
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

        val back = binding.back
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        val loading = LoadingDialog(this)
        loading.startLoading()

        val Semester = binding.semester
        val TahunAjaran = binding.tahunajaran
        val TotalSKS = binding.totalSKS
        val Ipk = binding.nilaiIPK

        khs = intent.getParcelableExtra<Khs>(TableKhsActivity.EXTRA_TABLEKHS) as Khs

        val semester = khs.semester
        val tahun_ajaran = khs.tahun_ajaran
        val total_sks = khs.total_sks
        val id : Int = khs.id_khs
        val IPK : Double = khs.nilai_ips

        Semester.text = "Semester $semester"
        TahunAjaran.text = tahun_ajaran
        TotalSKS.text = total_sks.toString()
        Ipk.text = IPK.toString()

        data = DataManager(baseContext)
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-detail-khs"
        val params = RequestParams()
        val token = data.getString("token")
        params.put("token", token)
        android.util.Log.i("DATA_API", token.toString())
        params.put("id_khs", id)
        Log.i("DATA_API", id.toString())
        client.addHeader("Accept", "application/json")

        client.post(url, params, object : AsyncHttpResponseHandler() {

            override fun onSuccess(
                statusCode: Int,
                headers: Array<cz.msebera.android.httpclient.Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                val responseJson = JSONObject(result)
                val Result = responseJson.getJSONObject("result")
                val Khs = Result.getJSONObject("khs")
                val semester = Result.getJSONArray("detail_khs")
                for (i in 0 until semester.length()) {
                    val detailKhs = semester.getJSONObject(i)
                    val listKhsdetail = TableKHS(
                        Khs.getString("semester"),
                        Khs.getString("tahun_ajaran"),
                        detailKhs.getString("mata_kuliah"),
                        detailKhs.getString("dosen"),
                        detailKhs.getInt("jumlah_pertemuan"),
                        detailKhs.getInt("sks"),
                        detailKhs.getDouble("nilai_uts"),
                        detailKhs.getDouble("nilai_uas"),
                        detailKhs.getDouble("nilai_tugas"),
                        detailKhs.getDouble("nilai_kehadiran"),
                        detailKhs.getDouble("nilai_akhir"),
                        detailKhs.getString("nilai_huruf"),
                        detailKhs.getString("status")
                    )

                    khsTable.add(listKhsdetail)
                }
                showKhsList()
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



    private fun showKhsList() {
        val recyclerView : RecyclerView = binding.listKhs
        recyclerView.layoutManager = LinearLayoutManager(this@TableKhsActivity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = TableKhsAdapter(khsTable)
    }

 }