package id.ac.sttccirebon.mahasiswa.ui.tugas

import io.grpc.Deadline

data class Tugas(val tittle_tugas: String,
                 val nama_dosen: String,
                 val mata_kuliah: String,
                 val tanggal_dibuat: String,
                 val deadline: String)
