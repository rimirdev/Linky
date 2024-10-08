package com.rimir.linky.ui.bottomsheets

import android.app.Activity
import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rimir.linky.R
import com.rimir.linky.data.Link
import com.rimir.linky.databinding.BottomSheetDialogBinding
import com.rimir.linky.util.*
import java.net.URL


object LinkBottomSheetDialog {

    fun launch(activity: Activity, link: Link) {

        val bottomSheetDialog = BottomSheetDialog(activity)
        val dialogBinding = BottomSheetDialogBinding.inflate(LayoutInflater.from(activity))
        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog?.apply {
            setOnShowListener {
                val bottomSheet = findViewById<View?>(R.id.design_bottom_sheet)
                bottomSheet?.setBackgroundResource(android.R.color.transparent)
            }
        }

        dialogBinding.dialogOpenAction.setOnClickListener {
            try {
                openLinkIntent(activity, link.url)
            } catch (e: ActivityNotFoundException) {
                activity.showSnackBar(R.string.message_link_invalid)
            }
            bottomSheetDialog.dismiss()
        }

        dialogBinding.previewLink.setOnClickListener {

            if (fileExtensions.contains(
                    URLSUtils.getExtension(link.url).lowercase()
                )
            ) PreviewBottomSheetDialog.launch(activity, link, true, false)
            else PreviewBottomSheetDialog.launch(activity, link, false, false)

        }

        if (fileExtensions.contains(URLSUtils.getExtension(link.url).lowercase()))
            dialogBinding.downloadLink.visibility = View.VISIBLE
        else
            dialogBinding.downloadLink.visibility = View.GONE

        dialogBinding.downloadLink.setOnClickListener {

            if (fileExtensions.contains(
                    URLSUtils.getExtension(link.url).lowercase()
                )
            ) PreviewBottomSheetDialog.launch(activity, link, true, true)
            else PreviewBottomSheetDialog.launch(activity, link, false, true)

        }

        // Stream media file
        val url = URL(link.url)
        val host: String = url.getHost()

        if (fileMedia.contains(URLSUtils.getExtension(link.url).lowercase())) {
            dialogBinding.previewLink.visibility = View.GONE
            dialogBinding.downloadLink.visibility = View.VISIBLE
            dialogBinding.playLink.visibility = View.VISIBLE
            dialogBinding.playLinkVlc.visibility = View.VISIBLE
            dialogBinding.playLink.setOnClickListener {
                AudioBottomSheetDialog.launch(activity, link)
            }
            dialogBinding.playLinkVlc.setOnClickListener {
                try {
                    val i = Intent(Intent.ACTION_MAIN)
                    i.component = ComponentName(
                        "com.vlcdirect.vlcdirect",
                        "com.vlcdirect.vlcdirect.URLStreamerActivity"
                    )
                    i.putExtra("url", link.url)
                    activity.startActivity(i)
                } catch (ex: Exception) {
                    Toast.makeText(
                        activity.getApplicationContext(),
                        activity.resources.getString(R.string.vlc_not_installed),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        } else if (webMedia.contains(host)) {

            dialogBinding.playLink.visibility = View.VISIBLE

            dialogBinding.playLink.setOnClickListener {
                YoutubeBottomSheetDialog.launch(activity, link)
            }

        } else {

            dialogBinding.playLink.visibility = View.GONE

        }


        dialogBinding.dialogCopyAction.setOnClickListener {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(link.title, link.url))
            bottomSheetDialog.dismiss()
            activity.showSnackBar(R.string.message_link_copy)
        }

        dialogBinding.dialogShareAction.setOnClickListener {
            shareTextIntent(activity, "${link.title}\n${link.url}")
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.dismissWithAnimation = true
        bottomSheetDialog.show()
    }
}