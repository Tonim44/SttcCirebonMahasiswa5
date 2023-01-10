package id.ac.sttccirebon.mahasiswa

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import id.ac.sttccirebon.mahasiswa.databinding.ActivityLoginBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.Constant
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.helper.HPI
import id.ac.sttccirebon.mahasiswa.ui.helper.PrefHelper
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var data : DataManager
    lateinit var prefHelper: PrefHelper
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        data = DataManager(baseContext)
        val view = binding.root
        prefHelper = PrefHelper(this)
        setContentView(view)

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

        binding.login.setOnClickListener {

            val username = binding.insertUsername.text
            val password = binding.insertPassword.text

            if (isNetworkAvailable()) {
                var isValid = true

                if (username.isEmpty()) {
                    isValid = false
                    binding.insertUsername.error = "NIM wajib diisi"
                }

                if (password.toString().isEmpty()) {
                    isValid = false
                    binding.insertPassword.error = "Password wajib diisi"
                }

                if (isValid) {
                    val loading = LoadingDialog(this)
                    loading.startLoading()
                    val client = AsyncHttpClient()
                    val url = "${HPI.API_URL}/api/mahasiswa/login"
                    val params = RequestParams()
                    params.put("type", "student")
                    params.put("username", username)
                    Log.i("DATA_API", username.toString())
                    params.put("password", password)
                    Log.i("DATA_API", password.toString())
                    client.addHeader("Accept", "application/json")
                    Log.i("DATA_SERVER", "Sebelum request")
                    binding.login.isEnabled = false

                    client.post(url, params, object : AsyncHttpResponseHandler() {
                        override fun onSuccess(
                            statusCode: Int,
                            headers: Array<Header>,
                            responseBody: ByteArray
                        ) {
                            val result = String(responseBody)
                            val responseJson = JSONObject(result)
                            val results = responseJson.getJSONObject("result")
                            val Token = results.getString("token")
                            Log.i("DATA_API", Token)

                            if (username.isNotEmpty() && password.toString().isNotEmpty()) {
                                saveSession(username.toString(), password.toString())
                            }

                            data.putUserData(results)
                            binding.login.isEnabled = true
                            moveIntent()

                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Array<Header>,
                            responseBody: ByteArray,
                            error: Throwable?
                        ) {
                            binding.login.isEnabled = true
                            binding.login.isEnabled = true
                            Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_LONG)
                                .show()
                        }
                    })
                    loading.isDismiss()
                }

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

    override fun onStart() {
        super.onStart()
        if (prefHelper.getBoolean( Constant.PREF_IS_LOGIN )) {
            moveIntent()
        }
    }

    private fun saveSession(username: String, password: String){
        prefHelper.put( Constant.PREF_USERNAME, username )
        prefHelper.put( Constant.PREF_PASSWORD, password )
        prefHelper.put( Constant.PREF_IS_LOGIN, true)
    }

    private fun moveIntent(){
        startActivity(Intent(this, DashboardActivity::class.java))
        val loading = LoadingDialog(this)
        loading.startLoading()
        (object :Runnable{
            override fun run() {
                finish()
                loading.isDismiss()
            }
        })

    }

}