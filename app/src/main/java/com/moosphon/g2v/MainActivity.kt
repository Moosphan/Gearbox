package com.moosphon.g2v

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.moosphon.g2v.base.BaseActivity
import com.moosphon.g2v.base.Configs
import com.moosphon.g2v.dialog.LottieAnimationDialog
import com.moosphon.g2v.engine.image.loader.ImageLoader
import com.moosphon.g2v.page.AboutMeActivity
import com.moosphon.g2v.page.LocalPictureActivity
import com.moosphon.g2v.page.VideoPreviewActivity
import com.moosphon.g2v.util.*
import com.otaliastudios.gif.GIFCompressor
import com.otaliastudios.gif.GIFListener
import com.ucard.timeory.loader.image.loader.LoadOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import permissions.dispatcher.*
import java.io.IOException

/**
 * home page for app.
 */
@RuntimePermissions
class MainActivity : BaseActivity() {
    override fun applyDefaultStatusStyle(): Boolean {
        return false
    }

    private var mGifPath: String = ""
    private var mVideoPath: String? = null
    private val mLoadingDialog: LottieAnimationDialog by lazy {
        LottieAnimationDialog(this, "loading-google-style.json")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.G2VideoTheme)
        ScreenUtils.applyStatusBarStyle(this)
        setContentView(R.layout.activity_main)
        initializeWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun initialize() {
        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.about  -> navigateTo<AboutMeActivity>()
                R.id.filter -> displayOptionsDialog()
            }
            true
        }

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
                //ToastUtils.showShort("准备将GIF转为视频格式")
                transformGifToVideo()
            }
        }
    }

    /**
     * display filter options for gif compressor output.
     */
    private fun displayOptionsDialog() {
        //todo: show bottom dialog with clips
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
                    return
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
        shapeDrawable.setColor(getColorResource(R.color.colorPrimary))
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
        mLoadingDialog.startLoading()
        createVideoFile()
        GIFCompressor.into(mVideoPath!!)
            .addDataSource(this, mGifPath)
            .setListener(object : GIFListener {
                override fun onGIFCompressionFailed(exception: Throwable) {
                    mLoadingDialog.cancelLoading()
                }

                override fun onGIFCompressionProgress(progress: Double) {
                }

                override fun onGIFCompressionCompleted() {
                    mLoadingDialog.cancelLoading()
                    ToastUtils.showShort("转换成功")
                    navigateTo<VideoPreviewActivity>(
                        "videoPath" to mVideoPath
                    )
                }

                override fun onGIFCompressionCanceled() {
                    mLoadingDialog.cancelLoading()
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

    companion object {
        const val RC_GIF_BEHAVIOR = 223
    }
}
