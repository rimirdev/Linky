package com.rimir.linky.util

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.rimir.linky.R
import com.rimir.linky.data.Link
import com.rimir.linky.databinding.AudioBottomSheetDialogBinding
import com.rimir.linky.databinding.YoutubeBottomSheetDialogBinding


object AudioBottomSheetDialog {

    private var exoPlayer: ExoPlayer? = null

    private var playPauseState = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    fun launch(activity: Activity, link: Link) {
        val bottomSheetDialog = BottomSheetDialog(activity)
        val dialogBinding = AudioBottomSheetDialogBinding.inflate(LayoutInflater.from(activity))
        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog.setOnDismissListener {
            releasePlayer()
        }

        bottomSheetDialog?.apply {
            setOnShowListener {
                val bottomSheet = findViewById<View?>(R.id.design_bottom_sheet)
                bottomSheet?.setBackgroundResource(android.R.color.transparent)
            }
        }

        exoPlayer = ExoPlayer.Builder(activity.applicationContext).build()
        exoPlayer?.let {
            dialogBinding.basicAudioPlayerWithClipLoopMergePlayerView.player = exoPlayer
            val mediaSource = buildMediaSource(link.url)
            it.setMediaSource(mediaSource)
            it.playWhenReady = playPauseState
            it.seekTo(currentWindow, playbackPosition)
            it.prepare()
        }

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.dismissWithAnimation = true
        bottomSheetDialog.show()
    }

    private fun buildMediaSource(url: String): MediaSource {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        return ProgressiveMediaSource.Factory(
            dataSourceFactory
        ).createMediaSource(MediaItem.fromUri(uriParser(url)))
    }

    // STEP 4: release the player when not needed
    private fun releasePlayer() {
        exoPlayer?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentMediaItemIndex
            playPauseState = this.playWhenReady
            release()
        }
        exoPlayer = null
    }

    fun uriParser(url: String): Uri {
        return Uri.parse(url)
    }

}