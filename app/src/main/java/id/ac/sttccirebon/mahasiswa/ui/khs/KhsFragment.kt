package id.ac.sttccirebon.mahasiswa.ui.khs

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
import id.ac.sttccirebon.mahasiswa.databinding.FragmentKhsBinding
import id.ac.sttccirebon.mahasiswa.databinding.FragmentKrsBinding
import id.ac.sttccirebon.mahasiswa.ui.krs.Krs
import id.ac.sttccirebon.mahasiswa.ui.krs.KrsAdapter
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialogFragment
import org.json.JSONObject

class KhsFragment : Fragment() {

    private var _binding: FragmentKhsBinding? = null
    private lateinit var data: DataManager
    private var khsList = arrayListOf<Khs>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loading = LoadingDialogFragment(this)
        loading.startLoading()

        _binding = FragmentKhsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        data = DataManager(root.context)
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-khs"
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
                val semester = Result.getJSONArray("khs")
                for (i in 0 until semester.length()) {
                    val hasilKhs = semester.getJSONObject(i)
                    val listKhs = Khs(
                        hasilKhs.getInt("id_khs"),
                        hasilKhs.getInt("semester"),
                        hasilKhs.getString("tahun_ajaran"),
                        hasilKhs.getInt( "total_sks"),
                        hasilKhs.getDouble("nilai_ips")
                    )

                    khsList.add(listKhs)
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

        return root
    }

    private fun showKhsList() {
        val recyclerView : RecyclerView = binding.khsSemester
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = KhsAdapter(khsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}