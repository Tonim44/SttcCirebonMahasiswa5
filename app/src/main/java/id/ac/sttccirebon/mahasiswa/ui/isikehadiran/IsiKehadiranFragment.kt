package id.ac.sttccirebon.mahasiswa.ui.isikehadiran

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import id.ac.sttccirebon.mahasiswa.DashboardActivity
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.databinding.FragmentIsikehadiranBinding
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialogFragment
import kotlinx.android.synthetic.main.activity_isikehadiran.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class IsiKehadiranFragment : Fragment() {

    private var _binding: FragmentIsikehadiranBinding? = null
    private lateinit  var  data : DataManager
    private var absenList = arrayListOf<Absen>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loading = LoadingDialogFragment(this)
        loading.startLoading()

        _binding = FragmentIsikehadiranBinding.inflate(inflater, container, false)
        val root: View = binding.root
        data = DataManager(root.context)
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

        val Tanggal : TextView = binding.presentDate
        Tanggal.text = currentDateandTime
        android.util.Log.i("DATA_API", Tanggal.toString())

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

        return root
    }

    private fun showAbsenList() {
        val recyclerView : RecyclerView = binding.jadwalPerkuliahan
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = AbsenAdapter(absenList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}