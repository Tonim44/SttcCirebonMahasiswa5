package id.ac.sttccirebon.mahasiswa.ui.logout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import id.ac.sttccirebon.mahasiswa.DashboardActivity
import id.ac.sttccirebon.mahasiswa.LoginActivity
import id.ac.sttccirebon.mahasiswa.databinding.FragmentLogoutBinding
import id.ac.sttccirebon.mahasiswa.ui.helper.PrefHelper
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialog
import id.ac.sttccirebon.mahasiswa.ui.uitel.LoadingDialogFragment

class LogoutFragment : Fragment() {

    private var _binding: FragmentLogoutBinding? = null
    lateinit var prefHelper: PrefHelper
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLogoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        prefHelper = PrefHelper(root.context)

        val Yalogout = binding.yaLogout
        Yalogout.setOnClickListener{
            val loading = LoadingDialogFragment(this)
            loading.startLoading()
            (object :Runnable{
                override fun run() {
                    loading.isDismiss()
                }
            })

            prefHelper.clear()
            startActivity(Intent(this.context, LoginActivity::class.java))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}