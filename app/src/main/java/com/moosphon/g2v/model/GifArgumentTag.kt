package com.moosphon.g2v.model

import android.graphics.Color
import androidx.annotation.StringRes
import com.moosphon.g2v.R

/**
 * Entity model for Gif argument to display chip tags.
 */
sealed class GifArgumentTag(var isChecked: Boolean) {

    enum class GifArgumentCategory(@StringRes val labelResId: Int) {
        NONE(0),
        FRAME_RATE(R.string.category_header_frame_rate),
        RESOLUTION(R.string.category_header_resolution),
        ASPECT_RATIO(R.string.category_header_aspect_ratio),
        ROTATION(R.string.category_header_rotation),
        SPEED(R.string.category_header_speed)
    }

    //val isChecked = ObservableBoolean(isChecked)
    //val isChecked = isChecked

    /** Determines the category heading to show in the filters sheet. */
    abstract fun getFilterCategory(): GifArgumentCategory

    /** Return the background color when filled. */
    abstract fun getColor(): String

    /** Return a color to use when the filter is selected, or TRANSPARENT to use the default. */
    open fun getSelectedTextColor(): String = "#00000000"

    /** Return a string resource to display, or 0 to use the value of [getText]. */
    open fun getTextResId(): Int = 0

    /** Return a string to display when [getTextResId] returns 0. */
    open fun getText(): String = ""

    /** Return a short string resource to display, or 0 to use the value of [getShortText]. */
    open fun getShortTextResId(): Int = 0

    /** Return a short string string to display when [getShortTextResId] returns 0. */
    open fun getShortText(): String = ""

    /** Return a value of float to change configs. */
    open fun getValue(): Float = 0f

    /** Filter options for gif config tags. */
    class TagFilter(val tag: Tag, isChecked: Boolean) : GifArgumentTag(isChecked) {

        override fun getFilterCategory(): GifArgumentCategory {
            return when (tag.category) {
                Tag.CATEGORY_FRAME_RATE -> GifArgumentCategory.FRAME_RATE
                Tag.CATEGORY_ASPECT_RATIO -> GifArgumentCategory.ASPECT_RATIO
                Tag.CATEGORY_RESOLUTION -> GifArgumentCategory.RESOLUTION
                Tag.CATEGORY_ROTATION -> GifArgumentCategory.ROTATION
                Tag.CATEGORY_SPEED -> GifArgumentCategory.SPEED
                else -> throw IllegalArgumentException("unsupported tag type in filters")
            }
        }

        override fun getColor(): String = tag.color

        override fun getSelectedTextColor(): String = tag.fontColor ?: super.getSelectedTextColor()

        override fun getTextResId(): Int = 0
        override fun getShortTextResId(): Int = 0

        override fun getText(): String = tag.displayName
        override fun getShortText(): String = tag.displayName

        override fun getValue(): Float = tag.value

        /** Only the tag is used for equality. */
        override fun equals(other: Any?) =
            this === other || (other is TagFilter && other.tag == tag)

        /** Only the tag is used for equality. */
        override fun hashCode() = tag.hashCode()

        // for DiffCallback
        fun isUiContentEqual(other: TagFilter) =
            tag.isUiContentEqual(other.tag)
    }
}