package com.moosphon.g2v.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.moosphon.g2v.engine.image.loader.ImageLoader
import com.moosphon.g2v.util.applyViewGone
import com.ucard.timeory.loader.image.loader.LoadOptions

/**
 * A basic ViewHolder for [RecyclerView.ViewHolder] to handle view configs.
 */
open class CommonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val viewList: SparseArray<View> = SparseArray()


    /**
     * get view context.
     */
    fun getContext() : Context = itemView.context


    /**
     * find target view in container if exist, or get it from the [itemView].
     */
    inline fun <reified T : View> findView(@IdRes id: Int) : T {
        var view = viewList.get(id)
        if (view == null) {
            view = itemView.findViewById(id)
            viewList.put(id, view)
        }
        return view as T
    }

    // more customized extensions for view initializations.


    fun setText(@IdRes id: Int, textContent: CharSequence): CommonViewHolder {
        val textView  = findView<TextView>(id)
        textView.text = textContent
        return this
    }

    fun loadImage(@IdRes id: Int, imgRes: Int): CommonViewHolder {
        val imageView = findView<ImageView>(id)
        imageView.setImageResource(imgRes)
        return this
    }

    fun loadImage(@IdRes id: Int, imgRes: Drawable): CommonViewHolder {
        val imageView = findView<ImageView>(id)
        imageView.setImageDrawable(imgRes)
        return this
    }

    fun loadImage(@IdRes id: Int, imgRes: String, thumbnailRatio: Float = 1.0f): CommonViewHolder {
        val imageView = findView<ImageView>(id)
        ImageLoader
            .INSTANCE
            .with {
                url = imgRes
                cacheStyle = LoadOptions.LoaderCacheStrategy.RESULT
                thumbnail = thumbnailRatio
            }.into(imageView)
        return this
    }

    fun isGone(@IdRes id: Int, gone: Boolean): CommonViewHolder {
        val view = findView<View>(id)
        view.applyViewGone(gone)
        return this
    }

    fun isGone(ids: List<Int>, gone: Boolean) {
        ids.forEach {
            val view = findView<View>(it)
            view.applyViewGone(gone)
        }
    }

}