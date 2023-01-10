package id.ac.sttccirebon.mahasiswa.ui.krs

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import id.ac.sttccirebon.mahasiswa.DashboardActivity
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.databinding.FragmentKrsBinding
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialogFragment
import org.json.JSONObject

class KrsFragment : Fragment() {

    private var _binding: FragmentKrsBinding? = null
    private lateinit var data: DataManager
    private var krsList = arrayListOf<Krs>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loading = LoadingDialogFragment(this)
        loading.startLoading()

        _binding = FragmentKrsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        data = DataManager(root.context)
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-krs"
        val params = RequestParams()
        val token = data.getString("token")
        params.put("token", token)
        android.util.Log.i("DATA_API", token.toString())
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
                    val semester = Result.getJSONArray("krs")
                    for (i in 0 until semester.length()) {
                        val jadwalKrs = semester.getJSONObject(i)
                        val listKrs = Krs(
                            jadwalKrs.getInt("id_krs"),
                            jadwalKrs.getInt("semester"),
                            jadwalKrs.getString("tahun_ajaran"),
                            jadwalKrs.getInt( "total_sks")
                        )
                        krsList.add(listKrs)
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

            return root
        }

    private fun onBackPressed() {
        val intent = Intent(this.context, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun showKrsList() {
        val recyclerView : RecyclerView = binding.krsSemester
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = KrsAdapter(krsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}