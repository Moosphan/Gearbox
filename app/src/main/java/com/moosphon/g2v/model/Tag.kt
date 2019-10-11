package com.moosphon.g2v.model

/**
 * Describes a tag, which contains meta-information about a conference session. A tag has two
 * components, a category, and a name, and together these give a tag its semantic meaning. For
 * example, a session may contain the following tags: {category: "TRACK", name: "ANDROID"} and
 * {category: "TYPE", name: "OFFICEHOURS"}. The first tag defines the session track as Android, and
 * the second tag defines the session type as an office hour.
 */
data class Tag(
    /**
     * Unique string identifying this tag.
     */
    val id: String,

    /**
     * Tag category type. For example, "track", "level", "type", "theme", etc.
     */
    val category: String,

    /**
     * Tag name. For example, "topic_iot", "type_afterhours", "topic_ar&vr", etc. Used to resolve
     * references to this tag from other entities during data deserialization and normalization.
     * For UI, use [displayName] instead.
     */
    val tagName: String,

    /**
     * This tag's order within its [category].
     */
    val orderInCategory: Int,

    /**
     * Display name within a category. For example, "Android", "Ads", "Design".
     */
    val displayName: String,

    /**
     * The color associated with this tag as a color integer.
     */
    val color: Int,

    /**
     * The text color associated with this tag as a color integer.
     */
    val fontColor: Int? = null
) {

    companion object {
        /** Category value for gif argument tags */
        const val CATEGORY_FRAME_RATE   = "gif_frame_rate"
        const val CATEGORY_RESOLUTION   = "gif_resolution"
        const val CATEGORY_ASPECT_RATIO = "gif_aspect_ratio"
        const val CATEGORY_ROTATION     = "gif_rotation"
        const val CATEGORY_SPEED        = "gif_play_speed"



    }

    /** Only IDs are used for equality. */
    override fun equals(other: Any?): Boolean = this === other || (other is Tag && other.id == id)

    /** Only IDs are used for equality. */
    override fun hashCode(): Int = id.hashCode()

    fun isUiContentEqual(other: Tag) = color == other.color && displayName == other.displayName

    fun isLightFontColor() = fontColor?.toLong() == 0xFFFFFFFF
}