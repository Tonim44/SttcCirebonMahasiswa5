package id.ac.sttccirebon.mahasiswa.ui.krs

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailKrs(val matakuliah: String,
               val dosen: String,
               val jumlah_pertemuan: Int,
               val jumlah_sks: Int) : Parcelable
