package com.moosphon.g2v.ui

import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.ToastUtils
import com.moosphon.g2v.R
import com.moosphon.g2v.base.BaseActivity
import kotlinx.android.synthetic.main.activity_video_preview.*


class VideoPreviewActivity : BaseActivity() {

    private var mTransformedVideo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_preview)

        initialize()
    }

    override fun applyDefaultStatusStyle(): Boolean = true

    private fun initialize() {
        if (!TextUtils.isEmpty(intent.getStringExtra("videoPath"))) {
            mTransformedVideo = intent.getStringExtra("videoPath")!!
            setUpVideo()
        } else {
            ToastUtils.showShort("出错了耶，重新尝试一下吧")
        }

        videoPreviewControlBtn.setOnClickListener {
            if (videoPreviewPlayer.isPlaying) {
                pauseVideo()
            } else {
                videoPreviewControlBtn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp)
                playVideo()
            }
        }

        videoPreviewBack.setOnClickListener { finish() }

        videoPreviewSaveBtn.setOnClickListener {
            ToastUtils.showShort("已保存至 $mTransformedVideo")
        }
    }

    private fun setUpVideo() {

        videoPreviewPlayer.setOnPreparedListener {
            videoPreviewControlBtn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp)
            videoPreviewPlayer.start()
        }

        videoPreviewPlayer.setOnCompletionListener {
            //stopPlaybackVideo()
            playVideo()
        }

        videoPreviewPlayer.setOnErrorListener { mediaPlayer, what, extra ->
            stopPlaybackVideo()
            true
        }

        videoPreviewPlayer.setVideoPath(mTransformedVideo)
    }

    private fun playVideo() {
        videoPreviewControlBtn.setImageResource(R.drawable.ic_pause_circle_outline_black_24dp)
        videoPreviewPlayer.setVideoPath(mTransformedVideo)
        videoPreviewPlayer.start()
    }

    private fun pauseVideo() {
        videoPreviewControlBtn.setImageResource(R.drawable.ic_play_circle_outline_black_24dp)
        if (videoPreviewPlayer.canPause()) {
            videoPreviewPlayer.pause()
        }
    }

    private fun stopPlaybackVideo() {
        try {
            videoPreviewControlBtn.setImageResource(R.drawable.ic_play_circle_outline_black_24dp)
            videoPreviewPlayer.stopPlayback()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!videoPreviewPlayer.isPlaying) {
            videoPreviewPlayer.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        pauseVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlaybackVideo()
    }
}
