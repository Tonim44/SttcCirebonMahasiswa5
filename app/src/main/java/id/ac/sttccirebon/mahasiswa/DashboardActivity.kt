package id.ac.sttccirebon.mahasiswa
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import id.ac.sttccirebon.mahasiswa.databinding.ActivityDashboardBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.ui.helper.PrefHelper
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import org.json.JSONObject

class DashboardActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var data: DataManager
    lateinit var prefHelper: PrefHelper
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        data = DataManager(baseContext)
        prefHelper = PrefHelper(this)

        supportActionBar?.hide()

        internetLayout = binding.InternetLayout
        noInternetLayout = binding.noInternetLayout
        tryAgainButton = binding.tryAgainButton

        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                drawLayout()
                loading.isDismiss()
            }
        }, 2000)

        tryAgainButton.setOnClickListener {
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

    @SuppressLint("NewApi")
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))

    }

    private fun drawLayout() {

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

        setSupportActionBar(binding.appBarDashboard.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )//, R.id.nav_isikehadiran, R.id.nav_pengumuman, R.id.nav_krs, R.id.nav_khs, R.id.nav_profile, R.id.nav_logout), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val token = data.getString("token")
        val client = AsyncHttpClient()
        val url = "${HPI.API_URL}/api/mahasiswa/get-profile"
        val params = RequestParams()
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

                val User: String = user.getString("nama_mahasiswa")
                val Name = navView.findViewById<TextView>(R.id.userName_bar)
                Name.text = User.toString()

                data.putUsername(User)

                val Nim = navView.findViewById<TextView>(R.id.nim_bar)
                Nim.text = user.getString("nim")

                val PhotoProfil = navView.findViewById<ImageView>(R.id.fotoProfile_bar)
                val fotoProfil = user.getString("link_foto_profil")
                Log.i("DATA_API", fotoProfil)
                Glide.with(this@DashboardActivity).load(fotoProfil).into(PhotoProfil)
                //Picasso.get().load(fotoProfil).into(PhotoProfil)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable?
            ) {
                Toast.makeText(applicationContext, "Gagal Ditampilkan", Toast.LENGTH_LONG).show()
            }
        })

    }


    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Keluar")
        alertDialogBuilder
            .setMessage("Apakah Anda Yakin Untuk Menutup Aplikasi ?")
            .setCancelable(false)
            .setPositiveButton(
                "Iya"
            ) { dialog, id ->
                moveTaskToBack(true)
                Process.killProcess(Process.myPid())
                System.exit(1)
            }
            .setNegativeButton(
                "Tidak"
            ) { dialog, id -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
