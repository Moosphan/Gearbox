package com.moosphon.g2v.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.moosphon.g2v.R
import com.moosphon.g2v.adapter.LocalImageAdapter
import com.moosphon.g2v.base.BaseActivity
import com.moosphon.g2v.selector.ImageMediaEntity
import com.moosphon.g2v.selector.MediaUtils
import com.moosphon.g2v.util.getColorResource
import com.moosphon.g2v.widget.MediaItemDecoration
import kotlinx.android.synthetic.main.activity_local_picture.*
import permissions.dispatcher.*
import java.lang.ref.WeakReference

/***
 * select a local picture as card cover.
 * @author moosphon
 * @date 2019.06.03
 */
@RuntimePermissions
class LocalPictureActivity : BaseActivity() {
    companion object {
        const val GET_LOCAL_IMAGES: Int = 100
        private class WithoutLeakHandler( activity: LocalPictureActivity) : Handler(){
            private var mActivity: WeakReference<LocalPictureActivity> = WeakReference(activity)

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    GET_LOCAL_IMAGES -> {
                        val activity = mActivity.get()
                        if (activity != null){
                            if(activity.pictureData == null || activity.pictureData?.size!! == 0){
                                ToastUtils.showShort("本地没有GIF图片资源")
                            } else {
                                activity.adapter.setData(activity.pictureData!!)
                                activity.localPictureRecyclerView.adapter = activity.adapter
                            }
                        }
                    }
                }
            }
        }
    }
    private var pictureData: List<ImageMediaEntity>? = ArrayList()
    private var handler: Handler = WithoutLeakHandler(this)
    private val adapter = LocalImageAdapter()
    private var currentPicture: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_picture)
        initView()
        searchForLocalPicturesWithPermissionCheck()
    }

    override fun applyDefaultStatusStyle(): Boolean = true

    private fun initView() {
        local_picture_selector_next.isEnabled = false
        local_picture_selector_back.setOnClickListener{
            finish()
        }
        local_picture_selector_next.setOnClickListener{
            //Logger.e("当前选中的本地封面：$currentPicture")
            if (!TextUtils.isEmpty(currentPicture)) {
                val callback = Intent()
                val bundle = Bundle()
                bundle.putString("localPicturePath", currentPicture)
                callback.putExtras(bundle)
                this.setResult(Activity.RESULT_OK, callback)
            }
            this.finish()
        }

        localPictureRecyclerView.layoutManager = GridLayoutManager(this, 4)
        localPictureRecyclerView.addItemDecoration(MediaItemDecoration(ConvertUtils.dp2px(4f), 4))
        adapter.listener = object : LocalImageAdapter.OnLocalCoverSelectListener{
            override fun onLocalCoverSelect(view: View, position: Int, state: Boolean) {
                if (state) {
                    //选中
                    currentPicture = pictureData!![position].path
                    if (!currentPicture.isNullOrEmpty()) {
                        val drawable = GradientDrawable()
                        drawable.setColor(getColorResource(R.color.colorPrimary))
                        drawable.cornerRadius = ConvertUtils.dp2px(26F).toFloat()
                        local_picture_selector_next.background = drawable
                        local_picture_selector_next.setTextColor(
                            ContextCompat.getColor(this@LocalPictureActivity,
                                R.color.textColorPrimaryLight))
                        local_picture_selector_next.isEnabled = true
                    }
                }else {
                    //取消选中
                    currentPicture = null
                    local_picture_selector_next.setTextColor(
                        ContextCompat.getColor(this@LocalPictureActivity,
                            R.color.textColorHintLight))
                    local_picture_selector_next.isEnabled = false
                }

            }

        }
    }

    /**
     * by moosphon on 2018/09/15
     * desc: 搜索系统本地所有图片
     * use ContentResolver in {@link #MediaStore.Video} <br/>
     */
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun searchForLocalPictures(){
        Thread(Runnable {
            val oldVideos = MediaUtils.getLocalGifs(this)
            Log.e("LocalPictureActivity", "扫描本地图片的数量为->${oldVideos?.size}")
            pictureData = oldVideos?.sortedByDescending { it.dateTaken }
            val message= Message()
            message.what = GET_LOCAL_IMAGES
            handler.sendMessage(message)
        }).start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }


    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onRecordingDenied(){
        ToastUtils.showShort("您拒绝了本次存储权限，将无法正常使用本地视频")
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onRecordingNeverAskAgain(){
        ToastUtils.showShort("后续不再提醒")
    }

    @Suppress("unused")
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onRecordingOfRationale(request: PermissionRequest){
        //showRationaleDialog(R.string.video_record_permission_rationale, request)
    }

}
