package com.rimir.linky.ui.bottomsheets

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.islamdidarmd.adblockerwebview.AdBlockerUtil
import com.rimir.linky.R
import com.rimir.linky.data.Link
import com.rimir.linky.databinding.PreviewBottomSheetDialogBinding
import com.rimir.linky.util.URLS


object PreviewBottomSheetDialog {

    fun launch(activity: Activity, link: Link, isFile: Boolean, download: Boolean) {
        val bottomSheetDialog = BottomSheetDialog(activity)
        val dialogBinding = PreviewBottomSheetDialogBinding.inflate(LayoutInflater.from(activity))
        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog?.apply {
            setOnShowListener {
                val bottomSheet = findViewById<View?>(R.id.design_bottom_sheet)
                bottomSheet?.setBackgroundResource(android.R.color.transparent)
            }
        }

        dialogBinding.webView.getSettings().setJavaScriptEnabled(true)
        val webSettings: WebSettings = dialogBinding.webView.getSettings()
        webSettings.javaScriptEnabled = true
        AdBlockerUtil.getInstance().initialize(activity.applicationContext)
        dialogBinding.webView.setAdBlockerEnabled(true)


        dialogBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                Log.d("ricardo", "onLoadResource: " + url)
            }
        }

        if (download) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                activity.requestPermissions(permissions, 5)
            }

            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.show()
        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            when (requestCode) {
                5 -> if (grantResults.size > 0 && permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    // check whether storage permission granted or not.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Download File
                        dialogBinding.webView.setDownloadListener(DownloadListener { url: String?, userAgent: String?, contentDisposition: String?, mimeType: String?, contentLength: Long ->
                            val source = Uri.parse(url)
                            val request = DownloadManager.Request(source)
                            val cookies = CookieManager.getInstance().getCookie(url)
                            request.addRequestHeader("cookie", cookies)
                            request.addRequestHeader("User-Agent", userAgent)
                            request.setDescription(activity.resources.getString(R.string.telechargement_en_cours))
                            request.setTitle(
                                URLUtil.guessFileName(
                                    url,
                                    contentDisposition,
                                    mimeType
                                )
                            )
                            request.allowScanningByMediaScanner()
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            request.setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                URLUtil.guessFileName(url, contentDisposition, mimeType)
                            )
                            val dm =
                                activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            dm.enqueue(request)
                            Toast.makeText(
                                activity.getApplicationContext(),
                                activity.resources.getString(R.string.telechargement_en_cours),
                                Toast.LENGTH_LONG
                            ).show()
                            bottomSheetDialog.dismiss()
                        })
                        dialogBinding.webView.loadUrl(link.url)

                    } else {

                        if (isFile) {
                            //Preview File
                            dialogBinding.webView.loadUrl(URLS.GOOGLE_VIEWR + link.url)
                            dialogBinding.webView.visibility = View.VISIBLE
                        } else {
                            // Preview website
                            dialogBinding.webView.loadUrl(link.url)
                            dialogBinding.webView.visibility = View.VISIBLE
                        }

                    }
                }

                else -> {
                    Toast.makeText(
                        activity.getApplicationContext(),
                        activity.resources.getString(R.string.vlc_not_installed),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

        }
    }

}