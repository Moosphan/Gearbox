package com.moosphon.g2v

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ToastUtils
import com.moosphon.g2v.base.Configs
import com.moosphon.g2v.engine.image.loader.ImageLoader
import com.moosphon.g2v.page.LocalPictureActivity
import com.moosphon.g2v.util.applyViewGone
import com.moosphon.g2v.util.getColorResource
import com.moosphon.g2v.util.loge
import com.moosphon.g2v.util.navigateTo
import com.otaliastudios.gif.GIFCompressor
import com.otaliastudios.gif.GIFListener
import com.ucard.timeory.loader.image.loader.LoadOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Logger
import org.jetbrains.anko.intentFor
import android.widget.Toast
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        const val RC_GIF_BEHAVIOR = 223
    }
    private var mGifPath: String = ""
    private val mVideoPath: String by lazy {
        // Create a temporary file for output.
        val filePath = Configs.VIDEO_DIR + System.currentTimeMillis().toString() + ".mp4"
        FileUtils.createOrExistsFile(filePath)

        filePath
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()
    }

    private fun initialize() {
        selectBtn.setOnClickListener {
            startActivityForResult(
                intentFor<LocalPictureActivity>(),
                RC_GIF_BEHAVIOR
            )
        }
        gif2VideoBtn.setOnClickListener {
            if (mGifPath.isEmpty()) {
                ToastUtils.showShort("请先从本地选择一张GIF")
            } else {
                ToastUtils.showShort("准备将GIF转为视频格式")
                transformGifToVideo()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_GIF_BEHAVIOR) {
                val gifPath = data?.extras?.getString("localPicturePath")
                if (TextUtils.isEmpty(gifPath)) {
                    loge("选择gif返回出错了>>>>>>")
                } else {
                    mGifPath = gifPath!!
                    updateUI()
                    displayGif()
                }
            }
        }
    }

    private fun updateUI() {
        gif2VideoBtn.setBackgroundColor(getColorResource(R.color.colorAccent))
        gif2VideoBtn.setTextColor(getColorResource(R.color.textColorPrimaryLight))
        previewBox.background = null
        previewBox.setBackgroundColor(getColorResource(R.color.colorBackground))
        selectBtn.applyViewGone(true)
        gifPreview.applyViewGone(false)

    }

    private fun displayGif() {
        ImageLoader.INSTANCE
            .with {
                url = mGifPath
                isGif = true
                cacheStyle = LoadOptions.LoaderCacheStrategy.NONE
                displayStyle = LoadOptions.LoaderImageScaleType.CENTER_FIT
            }
            .into(gifPreview)
    }

    private fun transformGifToVideo() {

        GIFCompressor.into(mVideoPath)
            .addDataSource(this, mGifPath)
            .setListener(object : GIFListener {
                override fun onGIFCompressionFailed(exception: Throwable) {
                    loge("转换失败了>>>>>>> ${exception.message}")
                }

                override fun onGIFCompressionProgress(progress: Double) {
                    loge("当前转换的进度：$progress")
                }

                override fun onGIFCompressionCompleted() {
                    loge("转换成功>>>>>>>>$mVideoPath")
                    ToastUtils.showShort("转换成功")
                }

                override fun onGIFCompressionCanceled() {
                }

            }).compress()
    }
}
