package com.moosphon.g2v

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.moosphon.g2v.base.Configs
import com.moosphon.g2v.engine.image.loader.ImageLoader
import com.moosphon.g2v.page.LocalPictureActivity
import com.moosphon.g2v.page.VideoPreviewActivity
import com.moosphon.g2v.util.applyViewGone
import com.moosphon.g2v.util.getColorResource
import com.moosphon.g2v.util.loge
import com.moosphon.g2v.util.navigateTo
import com.otaliastudios.gif.GIFCompressor
import com.otaliastudios.gif.GIFListener
import com.ucard.timeory.loader.image.loader.LoadOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import permissions.dispatcher.*
import java.io.IOException

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    companion object {
        const val RC_GIF_BEHAVIOR = 223
    }
    private var mGifPath: String = ""
    private var mVideoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun initialize() {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }


    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onRecordingDenied(){
        ToastUtils.showShort("您拒绝了本次存储权限，将无法正常使用本功能")
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onRecordingNeverAskAgain(){
        ToastUtils.showShort("您已拒绝授权，后续不再提醒")
    }

    @Suppress("unused")
    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onRecordingOfRationale(request: PermissionRequest){
        //showRationaleDialog(R.string.video_record_permission_rationale, request)
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
        val shapeDrawable = gif2VideoBtn.background as GradientDrawable
        shapeDrawable.setColor(getColorResource(R.color.colorAccent))
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
        //todo: 通过加载动画来过渡，提升用户体验
        videoPreviewProgressBar.applyViewGone(false)
        createVideoFile()
        GIFCompressor.into(mVideoPath!!)
            .addDataSource(this, mGifPath)
            .setListener(object : GIFListener {
                override fun onGIFCompressionFailed(exception: Throwable) {
                    loge("转换失败了>>>>>>> ${exception.message}")
                    videoPreviewProgressBar.applyViewGone(true)
                }

                override fun onGIFCompressionProgress(progress: Double) {
                    loge("当前转换的进度：$progress")
                }

                override fun onGIFCompressionCompleted() {
                    loge("转换成功>>>>>>>>$mVideoPath")
                    videoPreviewProgressBar.applyViewGone(true)
                    ToastUtils.showShort("转换成功")
                    navigateTo<VideoPreviewActivity>(
                        "videoPath" to mVideoPath
                    )
                }

                override fun onGIFCompressionCanceled() {
                    videoPreviewProgressBar.applyViewGone(true)
                }

            }).compress()
    }

    private fun createVideoFile() {
        val filePath = Configs.APP_DIR + Configs.VIDEO_DIR + System.currentTimeMillis().toString() + ".mp4"
        try {
            FileUtils.createOrExistsFile(filePath)
        }catch (e: IOException) {
            loge("创建失败，原因在于：$e")
        }
        mVideoPath = filePath
    }
}
