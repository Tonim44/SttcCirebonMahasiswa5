package id.ac.sttccirebon.mahasiswa.ui.isikehadiran

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import id.ac.sttccirebon.mahasiswa.DashboardActivity
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.databinding.ActivityIsikehadiranBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.Constant
import id.ac.sttccirebon.mahasiswa.ui.helper.PrefHelper
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import org.json.JSONObject
import org.osmdroid.views.MapController
import java.io.ByteArrayOutputStream
import java.io.File

class IsiKehadiranActvity : AppCompatActivity() {

    private lateinit var binding: ActivityIsikehadiranBinding
    private val FILE_NAME = "photo.jpg"
    private val REQUEST_CODE = 42
    private lateinit var photoFile: File
    private lateinit var absen: Absen
    private lateinit var data: DataManager
    lateinit var prefHelper: PrefHelper
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView


    companion object {
        const val EXTRA_ABSEN = "extra_absen"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityIsikehadiranBinding.inflate(layoutInflater)
        data = DataManager(baseContext)

        val view = binding.root
        prefHelper = PrefHelper(this)
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
        //SetData
        val Matakuliah = binding.detailMatkul
        val Namadosen = binding.detailDosen
        val Tanggal = binding.detailTanggal
        val JamKuliah = binding.detailJam
        val Ruangan = binding.detailRuangan

        val back = binding.back
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        absen = intent.getParcelableExtra<Absen>(EXTRA_ABSEN) as Absen

        val matkul = absen.matakuliah
        val dosen = absen.namadosen
        val tanggal = absen.tanggal
        val jamawal = absen.jammulai
        val jamakhir = absen.jamselesai
        val ruangan = absen.ruangan

        Matakuliah.text = matkul
        Namadosen.text = dosen
        Tanggal.text = tanggal
        JamKuliah.text = "${jamawal}-${jamakhir}"
        Ruangan.text = ruangan

        binding.simpanKehadiran.setOnClickListener{
            Toast.makeText(this@IsiKehadiranActvity, "Lakukan foto absen terlebih dahulu", Toast.LENGTH_LONG).show()
        }
        binding.sharelok.setOnClickListener{
            Toast.makeText(this@IsiKehadiranActvity, "Lakukan foto absen terlebih dahulu", Toast.LENGTH_LONG).show()
        }

        //TakePhoto
        val btnTakePicture = binding.ambilfoto
        btnTakePicture.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider =
                FileProvider.getUriForFile(this, "id.ac.sttccirebon.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val data = DataManager(baseContext)
            val imageView = binding.imageView
            val byteArrayOutputStream = ByteArrayOutputStream()
            var bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            bitmap = resizeBitmap(bitmap, 800)
            imageView.setImageBitmap(bitmap)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
            val imageString: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            Log.i("DATA_SERVER", imageString)
            data.putPhoto(imageString)

            sendAbsen()
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }

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

    fun sendAbsen() {

        //TakeLocation
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        task.addOnSuccessListener {

            val data = DataManager(baseContext)

            val Latitude: String = it.latitude.toString()
            val Longitude: String = it.longitude.toString()

//            val Latitude: String ="-6.740562"
//            val Longitude: String ="108.543294"

            Log.i("DATA_API", Latitude.toString())
            Log.i("DATA_API", Longitude.toString())

            val id: Int = absen.idjadwal
            val imageCapture = data.getString("photo")

            val shareLokasi = binding.sharelok
            shareLokasi.setOnClickListener {
                val intent = Intent(this, MapsIsiActivity::class.java)
                startActivity(intent)
            }

            //SimpanKehadiran
            val Save: CardView = binding.simpanKehadiran
            Save.setOnClickListener {

                if (isNetworkAvailable()) {
                    val loading = LoadingDialog(this)
                    loading.startLoading()

                    val client = AsyncHttpClient()
                    val url = "https://siak.adiva.co.id/api/mahasiswa/set-absensi"
                    val token = data.getString("token")
                    val params = RequestParams()

                    params.put("token", token)
                    Log.i("DATA_API", token.toString())

                    params.put("id_detail_jadwal_kuliah",id)
                    Log.i("DATA_API", id.toString())

                    params.put("imageCapture", imageCapture)
                    Log.i("DATA_API", imageCapture.toString())

                    params.put("latitude",Latitude)
                    Log.i("DATA_API", Latitude.toString())

                    params.put("longitude",Longitude)
                    Log.i("DATA_API", Longitude.toString())

                    client.addHeader("Accept", "application/json")
                    Log.i("DATA_SERVER", "Sebelum request")
                    binding.simpanKehadiran.isEnabled = false

                    if (token.toString().isNotEmpty() && id.toString().isNotEmpty()
                        && imageCapture.toString().isNotEmpty() && Latitude.toString().isNotEmpty()
                        && Longitude.toString().isNotEmpty()) {

                        saveSession( token.toString(), id.toString(), imageCapture.toString(),
                            Latitude, Longitude)

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
                            binding.simpanKehadiran.isEnabled = true
                            loading.isDismiss()
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<Header>,
                            responseBody: ByteArray,
                            error: Throwable?
                        ) {
                            Log.i("DATA_API", "Code ${statusCode.toString()}")
                            binding.simpanKehadiran.isEnabled = true
                            Log.i("DATA_API", "Code ${statusCode.toString()}")
                            val result = String(responseBody)
                            Log.i("DATA_API", "Error ${result}")
                            val responseJson = JSONObject(result)
                            val resultJson = responseJson.getJSONObject("result")
                            val message = resultJson.getString("message")
                            Log.i("DATA_API", "Code ${statusCode.toString()}")
                            makeToast(message)
                            loading.isDismiss()
                        }
                    })
                } else {
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
        }
    }

    private fun saveSession(
      token: String,
      id: String,
      imageCapture: String,
      latitude: String,
      longitude: String
    ){
        prefHelper.put( Constant.PREF_TOKEN, token )
        prefHelper.put( Constant.PREF_ID, id.toString() )
        prefHelper.put( Constant.PREF_IMAGECAPTURE, imageCapture )
        prefHelper.put( Constant.PREF_LATITUDE, latitude)
        prefHelper.put( Constant.PREF_LONGTITUDE, longitude)
        prefHelper.put( Constant.PREF_IS_LOGIN, true)
      }

    private fun moveIntent(){
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun makeToast(text: String) {
          Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
      }

}
