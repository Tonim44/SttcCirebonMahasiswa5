package id.ac.sttccirebon.mahasiswa.ui.helper

import android.graphics.Bitmap

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException


class HPI {

    companion object {

     const val API_URL = "https://siak.adiva.co.id"

        fun encodeImage(path: String): String? {
            val imagefile = File(path)
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(imagefile)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            val bm = BitmapFactory.decodeStream(fis)
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b: ByteArray = baos.toByteArray()
            return encodeToString(b, DEFAULT)
        }

        fun makeImageDir() : String {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/picFolder/"
            val newdir = File(dir)
            newdir.mkdirs()

            return dir
        }
    }
}