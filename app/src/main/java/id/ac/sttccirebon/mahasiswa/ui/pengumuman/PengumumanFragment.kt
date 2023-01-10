package id.ac.sttccirebon.mahasiswa.ui.pengumuman

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import id.ac.sttccirebon.mahasiswa.DashboardActivity
import id.ac.sttccirebon.mahasiswa.databinding.FragmentPengumumanBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.ui.krs.Krs
import id.ac.sttccirebon.mahasiswa.ui.krs.KrsAdapter
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialogFragment
import org.json.JSONObject


class PengumumanFragment : Fragment() {

  private var _binding: FragmentPengumumanBinding? = null
  private val binding get() = _binding!!
  private lateinit var data: DataManager
  private var pengumumanList = arrayListOf<Pengumuman>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    val loading = LoadingDialogFragment(this)
    loading.startLoading()

    _binding = FragmentPengumumanBinding.inflate(inflater, container, false)
    val root: View = binding.root

    data = DataManager(root.context)
    val client = AsyncHttpClient()
    val url = "${HPI.API_URL}/api/mahasiswa/get-pengumuman"
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
        val Result = responseJson.getJSONArray("result")
        for (i in 0 until Result.length()) {
          val pengumuman = Result.getJSONObject(i)
          val listPengumuman = Pengumuman(
            pengumuman.getString("judul"),
            pengumuman.getString("konten"),
            pengumuman.getString("tanggal"),
            pengumuman.getString( "file")
          )

          pengumumanList.add(listPengumuman)
        }
        showPegumumanList()
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

  private fun showPegumumanList() {
    val recyclerView : RecyclerView = binding.pengumuman
    recyclerView.layoutManager = LinearLayoutManager(this.context)
    recyclerView.setHasFixedSize(true)
    recyclerView.adapter = PengumumanAdapter(pengumumanList)
  }

  override fun onDestroyView() {
          super.onDestroyView()
          _binding = null
      }
  }