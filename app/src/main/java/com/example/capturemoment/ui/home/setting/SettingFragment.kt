package com.example.capturemoment.ui.home.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.capturemoment.R
import com.example.capturemoment.databinding.FragmentSettingBinding
import com.example.capturemoment.ui.authentication.AuthenticationActivity
import com.example.capturemoment.ui.home.HomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.NonCancellable.cancel
import kotlinx.coroutines.launch
import java.net.Authenticator
import java.util.*

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding as FragmentSettingBinding

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.getNameUser().collect { nameUser ->
                    if (!nameUser.isNullOrEmpty()) {
                        binding.apply {

                            tvName.text = nameUser
                        }
                    }
                }
            }
        }
        binding.localName.text = Locale.getDefault().displayName
        binding.setLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.btnLogout.setOnClickListener {
           showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.signout_title))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.sign_out)) { _, _ ->
                viewModel.saveAuthToken("")
                viewModel.saveNameUser("")
                Intent(requireContext(), AuthenticationActivity::class.java).also { intent ->
                    startActivity(intent)
                    requireActivity().finish()
                }
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.logout_message_success),
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}