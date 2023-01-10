package id.ac.sttccirebon.mahasiswa.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import id.ac.sttccirebon.mahasiswa.R
import java.io.ByteArrayOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    // Initialize variable
    var btnEncode: Button? = null
    var btnDecode: Button? = null
    var textView: TextView? = null
    var imageView: ImageView? = null
    var sImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        btnEncode = findViewById(R.id.btn_encode)
        btnDecode = findViewById(R.id.btn_decode)
        textView = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)

        // Code for Encode button
        btnEncode!!.setOnClickListener {
            // check condition
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                // when permission is nor granted
                // request permission
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    100
                )
            } else {
                // when permission
                // is granted
                // create method
                selectImage()
            }
        }

        // Code for Decode button
        btnDecode!!.setOnClickListener { // decode base64 string
            val bytes = Base64.decode(sImage, Base64.DEFAULT)
            // Initialize bitmap
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            // set bitmap on imageView
            imageView!!.setImageBitmap(bitmap)
        }
    }

    private fun selectImage() {
        // clear previous data
        textView!!.text = ""
        imageView!!.setImageBitmap(null)
        // Initialize intent
        val intent = Intent(Intent.ACTION_PICK)
        // set type
        intent.type = "image/*"
        // start activity result
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)
    }

   override fun onRequestPermissionsResult(
       requestCode: Int,
       permissions: Array<out String>,
       grantResults: IntArray
   ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // check condition
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // when permission
            // is granted
            // call method
            selectImage()
        } else {
            // when permission is denied
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check condition
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // when result is ok
            // initialize uri
            val uri = data.data
            // Initialize bitmap
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
                // initialize byte stream
                val stream = ByteArrayOutputStream()
                // compress Bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                // Initialize byte array
                val bytes = stream.toByteArray()
                // get base64 encoded string
                sImage = Base64.encodeToString(bytes, Base64.DEFAULT)
                // set encoded text on textview
                textView!!.text = sImage
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}