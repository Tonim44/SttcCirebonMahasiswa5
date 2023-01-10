package id.ac.sttccirebon.mahasiswa.ui.khs

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TableKHS(
            val semester: String,
            val tahun_ajaran : String,
            val matakuliah: String,
            val dosen: String,
            val jumlah_pertemuan: Int,
            val jumlah_sks: Int,
            val nilai_uts : Double,
            val nilai_uas : Double,
            val nilai_tugas : Double,
            val nilai_kehadiran : Double,
            val nilai_akhir : Double,
            val nilai_huruf : String,
            val status : String) : Parcelable
