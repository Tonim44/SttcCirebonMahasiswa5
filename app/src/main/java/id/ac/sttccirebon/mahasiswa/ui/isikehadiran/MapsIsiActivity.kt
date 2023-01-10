package id.ac.sttccirebon.mahasiswa.ui.isikehadiran

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import id.ac.sttccirebon.mahasiswa.R
import id.ac.sttccirebon.mahasiswa.ui.helper.DataManager
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_maps.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import java.util.*

class MapsIsiActivity  : AppCompatActivity() {

    lateinit var mapController: MapController
    private lateinit var data : DataManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_maps)

        supportActionBar?.hide()

        internetLayout = findViewById(R.id.InternetLayout)
        noInternetLayout = findViewById(R.id.noInternetLayout)
        tryAgainButton = findViewById(R.id.try_again_button)

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

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener(View.OnClickListener { onBackPressed() })

        task.addOnSuccessListener {

            val data = DataManager(baseContext)
            val Latitude: String = it.latitude.toString()
            val Longitude: String = it.longitude.toString()

            var addresses: List<Address>
            val geocoder = Geocoder(this@MapsIsiActivity, Locale.getDefault())

            // val Latitude: String = data.getString("latitude").toString()
            //val Longitude: String = data.getString("longitude").toString()

            //val Latitude: Double =-6.740562
            //val Longitude: Double =108.543294

            Log.i("DATA_API", Latitude.toString())
            Log.i("DATA_API", Longitude.toString())

            Configuration.getInstance()
                .load(this, PreferenceManager.getDefaultSharedPreferences(this))

            addresses = geocoder.getFromLocation(
                Latitude.toDouble(),
                Longitude.toDouble(),
                1
            )

            var address: String = addresses[0].getAddressLine(0)
            Log.i("DATA_API", address)

            val geoPoint = GeoPoint(Latitude.toFloat().toDouble(), Longitude.toFloat().toDouble())
            mapView.setMultiTouchControls(true)
            mapView.controller.animateTo(geoPoint)
            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            mapController = mapView.controller as MapController
            mapController.setCenter(geoPoint)
            mapController.zoomTo(15)

            val strVicinity: String = address
            val latLoc = Latitude.toFloat().toDouble()
            val longLoc = Longitude.toFloat().toDouble()

            val tvAlamat = findViewById<TextView>(R.id.AlamatLokasi)
            tvAlamat.text = strVicinity

            val marker = Marker(mapView)
            marker.icon = resources.getDrawable(R.drawable.ic_place)
            marker.position = GeoPoint(latLoc, longLoc)
            marker.infoWindow = CustomInfoWindow(mapView)
            marker.setOnMarkerClickListener { item, arg1 ->
                item.showInfoWindow()
                true
            }
            mapView.overlays.add(marker)
            mapView.invalidate()
        }
    }

    public override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if (mapView != null) {
            mapView.onResume()
        }
    }

    public override fun onPause() {
        super.onPause()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if (mapView != null) {
            mapView.onPause()
        }
    }

}