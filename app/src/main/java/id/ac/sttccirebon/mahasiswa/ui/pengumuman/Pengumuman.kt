package id.ac.sttccirebon.mahasiswa.ui.pengumuman

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pengumuman(val judul: String,
               val konten: String,
               val tanggal: String,
               val file: String) : Parcelable
