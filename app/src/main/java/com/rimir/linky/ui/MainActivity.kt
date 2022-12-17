package com.rimir.linky.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import com.rimir.linky.R
import com.rimir.linky.data.Theme
import com.rimir.linky.util.ACTION_CREATE_FOLDER
import com.rimir.linky.util.ACTION_CREATE_LINK
import com.rimir.linky.util.SettingUtils
import com.rimir.linky.util.findNavHostController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var settingUtils: SettingUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleLinkHubIntent()
        handleMultiThemeOption()
    }

    private fun handleLinkHubIntent() {
        when(intent.action) {
            Intent.ACTION_VIEW -> return
            Intent.ACTION_SEND -> {
                val sharedLink = intent.getStringExtra(Intent.EXTRA_TEXT)
                val bundle = bundleOf("shared_link" to sharedLink)
                findNavHostController(R.id.nav_host_fragment).navigate(R.id.linkFragment, bundle)
                return
            }
            Intent.ACTION_PROCESS_TEXT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val sharedLink = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
                    val bundle = bundleOf("shared_link" to sharedLink)
                    findNavHostController(R.id.nav_host_fragment).navigate(R.id.linkFragment, bundle)
                }
            }
            ACTION_CREATE_LINK -> {
                findNavHostController(R.id.nav_host_fragment).navigate(R.id.linkFragment)
            }
            ACTION_CREATE_FOLDER -> {
                findNavHostController(R.id.nav_host_fragment).navigate(R.id.folderFragment)
            }
        }
    }

    private fun handleMultiThemeOption() {
        val theme = settingUtils.getThemeType()
        val themeMode = if (theme == Theme.DARK) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}