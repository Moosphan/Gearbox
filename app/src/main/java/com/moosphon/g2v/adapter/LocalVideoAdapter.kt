package com.moosphon.g2v.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moosphon.g2v.R
import com.moosphon.g2v.engine.image.loader.ImageLoader
import com.moosphon.g2v.selector.VideoMediaEntity
import com.ucard.timeory.loader.image.loader.LoadOptions

/**
 * <pre>
 *    author: moosphon
 *    date:   2018/09/16
 *    desc:   本地视频的适配器
 * <pre/>
 */
class LocalVideoAdapter: RecyclerView.Adapter<LocalVideoAdapter.LocalVideoViewHolder>() {
    lateinit var context: Context
    private var mSelectedPosition: Int = -1
    var listener: OnLocalVideoSelectListener? = null
    private lateinit var data: List<VideoMediaEntity>
    private var checkState: HashSet<Int> = HashSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalVideoViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_local_video_layout, parent, false)
        return LocalVideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: LocalVideoViewHolder, position: Int) {
        val thumbnailImage: ImageView = holder.view.findViewById(R.id.local_video_item_thumbnail)
        val checkBox: CheckBox = holder.view.findViewById(R.id.local_video_item_cb)
        val container: RelativeLayout = holder.view.findViewById(R.id.local_video_item_container)
        val durationText: TextView = holder.view.findViewById(R.id.local_video_item_duration)
        checkBox.isChecked = checkState.contains(position)
        durationText.text = data[position].duration

        ImageLoader
                .INSTANCE
                .with {
                    url = data[position].path
                    error = R.drawable.ic_empty_zhihu
                    thumbnail = 0.2f
                    cacheStyle = LoadOptions.LoaderCacheStrategy.NONE
                }.into(thumbnailImage)
        checkBox.setOnClickListener {

            if (mSelectedPosition!=position){
                //先取消上个item的勾选状态
                checkState.remove(mSelectedPosition)
                notifyItemChanged(mSelectedPosition)
                //设置新Item的勾选状态
                mSelectedPosition = position
                checkState.add(mSelectedPosition)
                notifyItemChanged(mSelectedPosition)
                if (listener != null){
                    listener!!.onVideoSelect(holder.view, position, true)

                }
            }else if(checkBox.isChecked){
                checkState.add(position)
                if (listener != null){
                    listener!!.onVideoSelect(holder.view, position, true)

                }

            }else if(!checkBox.isChecked){

                checkState.remove(position)
                if (listener != null){
                    listener!!.onVideoSelect(holder.view, position, false)
                }
            }
        }
        container.setOnClickListener {
            if (mSelectedPosition!=position){
                //先取消上个item的勾选状态
                checkState.remove(mSelectedPosition)
                notifyItemChanged(mSelectedPosition)
                //设置新Item的勾选状态
                mSelectedPosition = position
                checkState.add(mSelectedPosition)
                notifyItemChanged(mSelectedPosition)
                if (listener != null){
                    listener!!.onVideoSelect(holder.view, position,true)

                }
            }else if(checkBox.isChecked){
                checkState.remove(position)
                checkBox.isChecked = false
                if (listener != null){
                    listener!!.onVideoSelect(holder.view, position,false)

                }

            }else if(!checkBox.isChecked){

                checkState.add(position)
                checkBox.isChecked = true
                if (listener != null){
                    listener!!.onVideoSelect(holder.view, position,true)
                }
            }


        }

    }

    fun setData(data: List<VideoMediaEntity>){
        this.data = data
        for (i in 0 until data.size) {
            if (data[i].isSelected) {
                mSelectedPosition = i
            }
        }
    }





    class LocalVideoViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    /** 自定义的本地视频选择监听器 */
    interface OnLocalVideoSelectListener{
        fun onVideoSelect(view:View, position:Int, state: Boolean)
    }

}