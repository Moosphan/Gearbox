package com.moosphon.g2v.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.moosphon.g2v.R
import com.moosphon.g2v.engine.image.loader.ImageLoader
import com.moosphon.g2v.selector.ImageMediaEntity
import com.moosphon.g2v.widget.MultiMediaCheckView

import com.ucard.timeory.loader.image.loader.LoadOptions
import org.jetbrains.anko.find

/**
 * <pre>
 *    author: moosphon
 *    date:   2018/09/16
 *    desc:   本地视频的适配器
 * <pre/>
 */
class LocalImageAdapter: RecyclerView.Adapter<LocalImageAdapter.LocalCoverViewHolder>() {
    lateinit var context: Context
    private var mSelectedPosition: Int = -1
    var listener: OnLocalCoverSelectListener? = null
    private lateinit var data: List<ImageMediaEntity>
    private var checkState: HashSet<Int> = HashSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalCoverViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_local_video_layout, parent, false)
        return LocalCoverViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LocalCoverViewHolder, position: Int) {
        val thumbnailImage: ImageView = holder.view.find(R.id.local_video_item_thumbnail)
        val checkBox: CheckBox = holder.view.find(R.id.local_video_item_cb)
        val container: RelativeLayout = holder.view.find(R.id.local_video_item_container)
        checkBox.isChecked = checkState.contains(position)
        ImageLoader
            .INSTANCE
            .with {
                url = data[position].path
                error = R.drawable.ic_empty_zhihu
                thumbnail = 0.3f
                cacheStyle = LoadOptions.LoaderCacheStrategy.RESULT
            }.into(thumbnailImage)
        checkBox.setOnClickListener {

            if (mSelectedPosition!=position){
                //先取消上个item的勾选状态

                //data[mSelectedPosition].isSelected = false
                checkState.remove(mSelectedPosition)
                notifyItemChanged(mSelectedPosition)
                //设置新Item的勾选状态
                mSelectedPosition = position
                checkState.add(mSelectedPosition)
                //data[mSelectedPosition].isSelected = true
                notifyItemChanged(mSelectedPosition)
                if (listener != null){
                    listener!!.onLocalCoverSelect(holder.view, position, true)

                }
            }else if(checkBox.isChecked){
                checkState.add(position)
                if (listener != null){
                    listener!!.onLocalCoverSelect(holder.view, position, true)

                }

            }else if(!checkBox.isChecked){

                checkState.remove(position)
                if (listener != null){
                    listener!!.onLocalCoverSelect(holder.view, position, false)

                }
            }

        }
        container.setOnClickListener {
            if (mSelectedPosition!=position){
                //先取消上个item的勾选状态

                //data[mSelectedPosition].isSelected = false
                checkState.remove(mSelectedPosition)
                notifyItemChanged(mSelectedPosition)
                //设置新Item的勾选状态
                mSelectedPosition = position
                checkState.add(mSelectedPosition)
                //data[mSelectedPosition].isSelected = true
                notifyItemChanged(mSelectedPosition)
                if (listener != null){
                    listener!!.onLocalCoverSelect(holder.view, position, true)

                }
            }else if(checkBox.isChecked){
                checkState.remove(position)
                checkBox.isChecked = false
                if (listener != null){
                    listener!!.onLocalCoverSelect(holder.view, position, false)

                }

            }else if(!checkBox.isChecked){
                checkState.add(position)
                checkBox.isChecked = true
                if (listener != null){
                    listener!!.onLocalCoverSelect(holder.view, position, true)

                }
            }

        }

    }

    fun setData(data: List<ImageMediaEntity>){
        this.data = data
        for (i in 0 until data.size) {
            if (data[i].isSelected) {
                mSelectedPosition = i
            }
        }
    }





    class LocalCoverViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    /** 自定义的本地视频选择监听器 */
    interface OnLocalCoverSelectListener{
        fun onLocalCoverSelect(view: View, position:Int, state: Boolean)
    }

}