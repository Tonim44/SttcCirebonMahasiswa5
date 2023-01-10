package id.ac.sttccirebon.mahasiswa.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.databinding.FragmentProfileBinding
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialogFragment
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var data : DataManager
    lateinit var PhotoProfil : ImageView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val loading = LoadingDialogFragment(this)
        loading.startLoading()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val EditProfile = binding.edit
        EditProfile.setOnClickListener{
            startActivity(Intent(this.context, EditProfileActivity::class.java))
        }

        data = DataManager(root.context)
        PhotoProfil = binding.fotoProfile
        val token = data.getString("token")
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-profile"
        val params  = RequestParams()
        params.put("token", token)
        Log.i("DATA_API", token.toString())
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
                val namaMahasiswa = user.getString("nama_mahasiswa")
                Log.i("DATA_API", namaMahasiswa)
                val nim = user.getString("nim")
                Log.i("DATA_API", nim.toString())
                val noTelp = user.getString("nomor_telepon")
                val jurusan = user.getString("jurusan")
                val angkatan = user.getString("angkatan")
                val semesterAktif = user.getString("semester_aktif")
                val kelas = user.getString("kelas")
                val fotoProfil = user.getString("link_foto_profil")



                binding.namaMahasiwa.text = namaMahasiswa
                binding.detailNim.text = nim
                binding.detailNomertelepon.text = "+${noTelp}"
                binding.detailJurusan.text = jurusan
                binding.detailAngkatan.text = angkatan
                binding.detailSemesteraktif.text = semesterAktif
                binding.detaiLKelas.text = kelas
                Picasso.get().load(fotoProfil).into(PhotoProfil)

                loading.isDismiss()

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable?
            ) {
                loading.isDismiss()
             }
        })
        
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}