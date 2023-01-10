package id.ac.sttccirebon.mahasiswa.ui.tugas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.ac.sttccirebon.mahasiswa.DashboardActivity
import id.ac.sttccirebon.mahasiswa.databinding.FragmentTugasBinding

class TugasFragment : Fragment() {

private var _binding: FragmentTugasBinding? = null

  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentTugasBinding.inflate(inflater, container, false)
    val root: View = binding.root

      Toast.makeText(this.context, "Masih tahap pengembangan", Toast.LENGTH_SHORT).show()

    return root
  }

    override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}