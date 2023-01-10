package id.ac.sttccirebon.mahasiswa.ui.khs

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize

data class Khs(
    val id_khs : Int,
    val semester : Int,
    val tahun_ajaran : String,
    val total_sks : Int,
    val nilai_ips : Double
) : Parcelable