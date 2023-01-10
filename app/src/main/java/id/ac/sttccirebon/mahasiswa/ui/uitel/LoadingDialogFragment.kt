package id.ac.sttccirebon.mahasiswa.ui.uitel

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import id.ac.sttccirebon.mahasiswa.R

class LoadingDialogFragment(val mFragment:Fragment) {

    private lateinit var isdialog:AlertDialog

    fun startLoading(){

        val infalter = mFragment.layoutInflater
        val dialogView = infalter.inflate(R.layout.loading_item,null)

        val bulider = AlertDialog.Builder(mFragment.context)
        bulider.setView(dialogView)
        bulider.setCancelable(false)
        isdialog = bulider.create()
        isdialog.show()
    }

    fun isDismiss(){
        isdialog.dismiss()
    }

}