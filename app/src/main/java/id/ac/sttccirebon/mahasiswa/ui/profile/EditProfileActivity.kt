package id.ac.sttccirebon.mahasiswa.ui.profile

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import id.ac.sttccirebon.mahasiswa.DashboardActivity
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDetailkhsBinding
import id.ac.sttccirebon.mahasiswa.databinding.ActivityEditprofileBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.Constant
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.ui.helper.PrefHelper
import id.ac.sttccirebon.mahasiswa.ui.isikehadiran.IsiKehadiranActvity
import id.ac.sttccirebon.mahasiswa.ui.khs.DetailKhsActivity
import id.ac.sttccirebon.mahasiswa.ui.khs.TableKHS
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_isikehadiran.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditprofileBinding
    private lateinit var data: DataManager
    private var avatarPhotoData: ByteArray? = null
    private var avatarPhotoBase64 : String? = null
    lateinit var prefHelper: PrefHelper

    companion object {
        private const val AVATAR_PHOTO_CODE = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityEditprofileBinding.inflate(layoutInflater)
        data = DataManager(baseContext)
        val view = binding.root
        prefHelper = PrefHelper(this)
        setContentView(view)

        val PhotoProfil = binding.fotoProfile
        val FotoProfile = data.getString("photo_profile")

        if (FotoProfile.toString().isNotEmpty()) {
            Picasso.get().load(FotoProfile).into(PhotoProfil)
        }

        binding.plus.setOnClickListener {
            openGallery(AVATAR_PHOTO_CODE)
        }

        binding.save.setOnClickListener{
            Toast.makeText(this@EditProfileActivity, "Tidak ada pergantian foto", Toast.LENGTH_LONG).show()
        }
    }

    private fun save() {
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/set-foto-profile"
        val token = data.getString("token")
        val avatar = avatarPhotoBase64
        val params  = RequestParams()

        val loading = LoadingDialog(this)
        loading.startLoading()

        params.put("token", token)
        Log.i("DATA_API", token.toString())

        params.put("foto_mahasiswa", avatar)
        Log.i("DATA_API", avatar.toString())
        data.putProfile(avatarPhotoBase64.toString())

        client.addHeader("Accept", "application/json")
        Log.i("DATA_SERVER", "Sebelum request")
        binding.save.isEnabled = false

        if (token.toString().isNotEmpty() && avatar.toString().isNotEmpty()) {
            saveSession( token.toString(), avatar.toString())
        }

        client.post(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                val responseJson = JSONObject(result)
                val results = responseJson.getJSONObject("result")
                val message = results.getString("message")
                Log.i("DATA_API", message)
                makeToast(message)
                Log.i("DATA_API", "Code ${statusCode.toString()}")
                moveIntent()
                binding.save.isEnabled = true
                loading.isDismiss()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable?
            ) {
                binding.save.isEnabled = true
                Toast.makeText(this@EditProfileActivity, "Tidak ada pergantian foto", Toast.LENGTH_LONG).show()
                loading.isDismiss()
            }
        })
    }

    private fun saveSession(
        token: String,
        avatar: String,
    ){
        prefHelper.put( Constant.PREF_TOKEN, token )
        prefHelper.put( Constant.PREF_AVATAR, avatar)
        prefHelper.put( Constant.PREF_IS_LOGIN, true)
    }

    private fun moveIntent(){
        startActivity(Intent(this, DashboardActivity::class.java))
        val loading = LoadingDialog(this)
        loading.startLoading()
        (object :Runnable{
            override fun run() {
                loading.isDismiss()
            }
        })
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun makeToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

    private fun openGallery(code: Int) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, code)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri, code: Int) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            when(code) {
                AVATAR_PHOTO_CODE -> {
                    avatarPhotoData = it.readBytes()
                    avatarPhotoBase64 = Base64.encodeToString(avatarPhotoData, Base64.DEFAULT)
                }
            }
            Log.i("DATA_SERVER", avatarPhotoBase64.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val uri = data?.data
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                AVATAR_PHOTO_CODE -> {
                    if (uri != null) {
                        val imageView = binding.fotoProfile
                        imageView.setImageURI(uri)
                        imageView.visibility = View.VISIBLE
                        createImageData(uri, requestCode)
                        // var bitmap = BitmapFactory.decodeFile(avatarPhotoData.toString())

                    }
                }
            }
        }

        binding.save.setOnClickListener {
            save()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun resizeBitmap(bitmap : Bitmap, maxSize: Int) : Bitmap {

        var width : Int = bitmap.getWidth();
        var height : Int = bitmap.getHeight();
        var x : Int = 0;

        if (width >= height && width > maxSize) {
            x = width / height;
            width = maxSize;
            height = maxSize / x
        } else if (height >= width && height > maxSize) {
            x = height / width;
            height = maxSize;
            width = maxSize / x
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

}