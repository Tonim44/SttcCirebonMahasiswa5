package id.ac.sttccirebon.mahasiswa.ui.isikehadiran

import id.ac.sttccirebon.mahasiswa.R
import kotlinx.android.synthetic.main.layout_tooltip.view.*
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInfoWindow(mapView: MapView?) : InfoWindow(R.layout.layout_tooltip, mapView) {

    override fun onClose() {
    }

    override fun onOpen(item: Any) {
        val marker = item as Marker
        val imageClose = mView.imageClose
        imageClose.setOnClickListener {
            marker.closeInfoWindow()
        }
    }

}