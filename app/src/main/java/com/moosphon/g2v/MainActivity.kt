package com.moosphon.g2v

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.moosphon.g2v.adapter.ArgumentRecord
import com.moosphon.g2v.base.BaseActivity
import com.moosphon.g2v.base.Configs
import com.moosphon.g2v.dialog.LottieAnimationDialog
import com.moosphon.g2v.engine.image.loader.ImageLoader
import com.moosphon.g2v.model.GifArgumentTag
import com.moosphon.g2v.ui.AboutMeActivity
import com.moosphon.g2v.ui.GifConfigurationFragment
import com.moosphon.g2v.ui.LocalPictureActivity
import com.moosphon.g2v.ui.VideoPreviewActivity
import com.moosphon.g2v.util.*
import com.moosphon.g2v.widget.BottomSheetBehavior
import com.otaliastudios.gif.GIFCompressor
import com.otaliastudios.gif.GIFListener
import com.otaliastudios.gif.strategy.DefaultStrategy
import com.otaliastudios.gif.strategy.Strategy
import com.otaliastudios.gif.strategy.size.AspectRatioResizer
import com.otaliastudios.gif.strategy.size.FractionResizer
import com.otaliastudios.gif.strategy.size.PassThroughResizer
import com.ucard.timeory.loader.image.loader.LoadOptions
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import permissions.dispatcher.*
import java.io.IOException


/**
 * home page for app.
 * todo: when argument is not empty, bottom sheet can be collapsed state and show arguments we choose on sheet bar.
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
    lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private var mConfigArguments: HashMap<GifArgumentTag.GifArgumentCategory, ArgumentRecord> = HashMap()
    private var mStrategy: Strategy? = null

    private var mSpeed: Float = 1f
    private var mRotation: Int = 0


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
                //transformGifToVideo()
                transformToVideo()
            }
        }

        // set up bottom sheet
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.gifArgumentSheet))

        // set up show case guide view
        setUpShowCaseGuide()

    }

    private fun setUpShowCaseGuide() {
        if (!SPUtils.getInstance().contains(Configs.NEW_USER_CASE_DISPLAY_KEY)) {
            SPUtils.getInstance().put(Configs.NEW_USER_CASE_DISPLAY_KEY, true)
            TapTargetView
                .showFor(this,
                    TapTarget
                        .forToolbarMenuItem(toolbar, R.id.filter, "初次见面请多指教 ◠◡◠\n", "点击这里可以自定义配置高级属性哟～")
                        .outerCircleColor(R.color.colorPrimary)
                        .outerCircleAlpha(0.82f)
                        .targetCircleColor(R.color.colorAccent)
                        .titleTextColor(R.color.textColorPrimaryLight)
                        .descriptionTextColor(R.color.textColorPrimaryLight)
                        .titleTextSize(20)
                        .descriptionTextSize(18)
                        .drawShadow(true)
                        .targetRadius(60)
                        .cancelable(false),
                    object : TapTargetView.Listener() {
                        override fun onTargetClick(view: TapTargetView?) {
                            super.onTargetClick(view)
                            loge("点击了引导层")
                        }
                    }
                )
        }

    }

    // deal with back pressed event if bottom sheet is expanded
    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED ||
                bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior.state =
                if (bottomSheetBehavior.skipCollapsed)
                    BottomSheetBehavior.STATE_HIDDEN
                else BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }

    }

    /**
     * the callback when arguments changed from [GifConfigurationFragment]
     */
    fun onSelectedArgumentsReceived(arguments: HashMap<GifArgumentTag.GifArgumentCategory, ArgumentRecord>) {
        loge("当前获得的参数为：${arguments.toString()}")
        mConfigArguments = arguments
    }

    /**
     * display filter options for gif compressor output.
     */
    private fun displayOptionsDialog() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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

    private fun setUpArgumentConfigs() {
        val strategyBuilder = DefaultStrategy.Builder()
        mConfigArguments.forEach { (category, argumentRecord) ->
            when(category) {
                GifArgumentTag.GifArgumentCategory.FRAME_RATE -> strategyBuilder.frameRate(
                    argumentRecord.value.toInt()
                )
                GifArgumentTag.GifArgumentCategory.ASPECT_RATIO -> {
                    val ratio = argumentRecord.value
                    strategyBuilder.addResizer(if (ratio > 0) AspectRatioResizer(ratio) else PassThroughResizer())
                }
                GifArgumentTag.GifArgumentCategory.RESOLUTION -> {
                    strategyBuilder.addResizer(FractionResizer(argumentRecord.value))
                }
                GifArgumentTag.GifArgumentCategory.ROTATION -> {
                    mRotation = argumentRecord.value.toInt()
                }
                GifArgumentTag.GifArgumentCategory.SPEED -> {
                    mSpeed = argumentRecord.value
                }
                else -> throw IllegalArgumentException("do not support this argument type")
            }
        }
        mStrategy = strategyBuilder.build()
    }

    private fun transformToVideo() {
        setUpArgumentConfigs()
        mLoadingDialog.startLoading()
        createVideoFile()
        GIFCompressor.into(mVideoPath!!)
            .setRotation(mRotation)
            .setSpeed(mSpeed)
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
