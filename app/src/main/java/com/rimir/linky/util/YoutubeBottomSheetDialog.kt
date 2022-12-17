package com.rimir.linky.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.rimir.linky.R
import com.rimir.linky.data.Link
import com.rimir.linky.databinding.YoutubeBottomSheetDialogBinding


object YoutubeBottomSheetDialog {

    fun launch(activity: Activity, link: Link) {
        val bottomSheetDialog = BottomSheetDialog(activity)
        val dialogBinding = YoutubeBottomSheetDialogBinding.inflate(LayoutInflater.from(activity))
        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog?.apply {
            setOnShowListener {
                val bottomSheet = findViewById<View?>(R.id.design_bottom_sheet)
                bottomSheet?.setBackgroundResource(android.R.color.transparent)
            }
        }

        val regex1 = "(.*)youtube.com/watch\\?v=(.*)".toRegex()
        val regex2 = "(.*)youtu\\.be/(.*)".toRegex()

        if (link.url.matches(regex1) || link.url.matches(regex2)
        ) {
            dialogBinding.youtubeVideo.visibility = View.VISIBLE
            dialogBinding.youtubeVideo.addYouTubePlayerListener(object :
                AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {

                    youTubePlayer.cueVideo(
                        if (link.url
                                .matches(regex1)
                        ) link.url.substring(
                            link.url.indexOf("v=") + 2
                        ) else link.url
                            .substring(link.url.indexOf("be/") + 3), 0f
                    )

                }

            })
        }
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.dismissWithAnimation = true
        bottomSheetDialog.show()
    }

}