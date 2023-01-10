package id.ac.sttccirebon.mahasiswa.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.ListAbsenActivity
import id.ac.sttccirebon.mahasiswa.databinding.FragmentHomeBinding
import id.ac.sttccirebon.mahasiswa.ui.isikehadiran.Absen
import id.ac.sttccirebon.mahasiswa.ui.isikehadiran.AbsenAdapter
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialogFragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var data : DataManager
    private var absenList = arrayListOf<Absen>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        data = DataManager(root.context)

        val ListAbsen = binding.klikIsikehadiran
        ListAbsen.setOnClickListener{
            startActivity(Intent(this.context, ListAbsenActivity::class.java))
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDateandTime: String = sdf.format(Date())
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-jadwal-kuliah"
        val params  = RequestParams()
        val token = data.getString("token")
        val username = data.getString("user")

        val User = binding.user
        User.text = "Selamat Datang\n" +
                "${username}"

        params.put("token", token)
        android.util.Log.i("DATA_API", token.toString())
        params.put("date", currentDateandTime)
        android.util.Log.i("DATA_API", currentDateandTime.toString())
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
                val jadwalKuliah = Result.getJSONArray("jadwal_kuliah")
                absenList.clear()

                for(i in 0 until jadwalKuliah.length()) {
                    val jadwalMatkul = jadwalKuliah.getJSONObject(i)
                    val lisMatkul = Absen(
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
                    absenList.add(lisMatkul)
                }
                showRecyclerList()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<cz.msebera.android.httpclient.Header>,
                responseBody: ByteArray,
                error: Throwable?
            ) {
                android.util.Log.i("DATA_API", "GAGAL DITAMPILKAN")
            }
        })

        root.calendarView.setOnDateChangeListener{
               calendarView, i, i2, i3 ->
            android.util.Log.i("DATA_API", i.toString())
            android.util.Log.i("DATA_API", (i2+1).toString())
            android.util.Log.i("DATA_API", i3.toString())

            val loading = LoadingDialogFragment(this)
            loading.startLoading()

            val Month : Int = i2+1
            val currentDateandTime : String = "$i-$Month-$i3"

            params.put("token", token)
            android.util.Log.i("DATA_API", token.toString())
            params.put("date", currentDateandTime)
            android.util.Log.i("DATA_API", currentDateandTime.toString())
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
                    val jadwalKuliah = Result.getJSONArray("jadwal_kuliah")
                    absenList.clear()

                    for(i in 0 until jadwalKuliah.length()) {
                        val jadwalMatkul = jadwalKuliah.getJSONObject(i)
                        val lisMatkul = Absen(
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

                        absenList.add(lisMatkul)
                    }

                    showRecyclerList()
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

        return root
    }

    private fun showRecyclerList() {
            val recyclerView: RecyclerView = binding.jadwalHarini
            recyclerView.layoutManager = LinearLayoutManager(this.context)
            //LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = JadwalAdapter(absenList)
    }

}

