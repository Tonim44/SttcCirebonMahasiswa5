package id.ac.sttccirebon.mahasiswa.ui.krs

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Krs(val id_krs: Int,
               val semester: Int,
               val tahun_ajaran: String,
               val total_sks: Int) : Parcelable
