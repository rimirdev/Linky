package com.rimir.linky.ui.setting

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.rimir.linky.BuildConfig
import com.rimir.linky.R
import com.rimir.linky.data.Theme
import com.rimir.linky.data.source.LinkRepository
import com.rimir.linky.databinding.FragmentSettingBinding
import com.rimir.linky.ui.home.HomeViewModel
import com.rimir.linky.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var settingUtils: SettingUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        setupUiInformation()
        setupListeners()

        return binding.root
    }

    fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
        } else {
            @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
        }

    private fun setupUiInformation() {
        // app version
        try {
            val pInfo = context!!.packageManager.getPackageInfoCompat(packageName)
            val versionNum = pInfo.versionName
            if (BuildConfig.DEBUG) {
                binding.version.text = ("version $versionNum")
            } else {
                binding.version.text = ("version $versionNum")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            binding.version.visibility = (View.GONE)
            e.printStackTrace()
        }

        // Setup Theme Switch
        val theme = settingUtils.getThemeType()
        binding.night.isChecked = theme == Theme.DARK

        val showCounter = settingUtils.getEnableClickCounter()
        binding.showCounterSwitch.isChecked = showCounter
    }

    private fun setupListeners() {
        binding.sourceCodeTxt.setOnClickListener {
            openLinkIntent(requireContext(), REPOSITORY_URL)
        }

        binding.importExportTxt.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_importExportFragment)
        }

        binding.contributorsTxt.setOnClickListener {
            openLinkIntent(requireContext(), REPOSITORY_CONTRIBUTORS_URL)
        }

        binding.issuesTxt.setOnClickListener {
            openLinkIntent(requireContext(), REPOSITORY_ISSUES_URL)
        }

        binding.shareTxt.setOnClickListener {
            shareTextIntent(requireContext(), PLAY_STORE_URL)
        }

        binding.night.setOnCheckedChangeListener { _, isChecked ->
            val themeMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(themeMode)

            val theme = if (isChecked) Theme.DARK else Theme.WHITE
            settingUtils.setThemeType(theme)
        }

        binding.showCounterSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingUtils.setEnableClickCounter(isChecked)
        }

        binding.clearApp.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(context!!)
                .setMessage(resources.getString(R.string.clear_cach))
                .setPositiveButton(
                    resources.getString(R.string.yes)
                ) { dialog, which ->
                    try {
                        deleteCache(context!!)
                        activity.showSnackBar(R.string.cache_cleard)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton(resources.getString(R.string.no), null)
                .show()
        })

        binding.eraseData.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(context!!)
                .setMessage(resources.getString(R.string.clear_all_links))
                .setPositiveButton(
                    resources.getString(R.string.yes)
                ) { dialog, which ->
                    try {
                        homeViewModel.deleteAll()

                        activity.showSnackBar(R.string.links_cleared)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .setNegativeButton(resources.getString(R.string.no), null)
                .show()
        })
    }

    fun deleteCache(context: Context) {
        try {
            val dir = context.cacheDir
            deleteDir(dir)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}