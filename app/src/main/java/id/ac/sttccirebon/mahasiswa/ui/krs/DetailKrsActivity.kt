package id.ac.sttccirebon.mahasiswa.ui.krs

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
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDetailtablekrsBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import org.json.JSONObject

class DetailKrsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailtablekrsBinding
    private lateinit var krs: Krs
    private lateinit var data : DataManager
    private var krsDetail = arrayListOf<DetailKrs>()
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView

    companion object {
        const val EXTRA_KRS = "extra_krs"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDetailtablekrsBinding.inflate(layoutInflater)
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

        krs = intent.getParcelableExtra<Krs>(DetailKrsActivity.EXTRA_KRS) as Krs

        val semester = krs.semester
        val tahun_ajaran = krs.tahun_ajaran
        val total_sks = krs.total_sks
        val id : Int = krs.id_krs

        Semester.text = "Semester $semester"
        TahunAjaran.text = tahun_ajaran
        TotalSKS.text = total_sks.toString()

        data = DataManager(baseContext)
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-detail-krs"
        val params = RequestParams()
        val token = data.getString("token")
        params.put("token", token)
        android.util.Log.i("DATA_API", token.toString())
        params.put("id_krs", id)
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
                val semester = Result.getJSONArray("detail_krs")
                for (i in 0 until semester.length()) {
                    val detailKrs = semester.getJSONObject(i)
                    val listKrsdetail = DetailKrs(
                        detailKrs.getString("mata_kuliah"),
                        detailKrs.getString("dosen"),
                        detailKrs.getInt("jumlah_pertemuan"),
                        detailKrs.getInt("sks")
                    )
                    krsDetail.add(listKrsdetail)
                }
                showKrsList()
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

    private fun showKrsList() {
        val recyclerView : RecyclerView = binding.listKrs
        recyclerView.layoutManager = LinearLayoutManager(this@DetailKrsActivity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = DetailKrsAdapter(krsDetail)
    }

 }