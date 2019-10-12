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
     * Display name within a category. For example, "Android", "Ads", "Design".
     */
    val displayName: String,

    /**
     * The color associated with this tag as a color integer.
     */
    val color: String,

    /**
     * The text color associated with this tag as a color integer.
     */
    val fontColor: String? = null,

    /**
     * The value for tag needs to get.
     */
    val value: Float
) {

    companion object {
        /** Category value for gif argument tags */
        const val CATEGORY_FRAME_RATE   = "Frame rate"
        const val CATEGORY_RESOLUTION   = "Resolution"
        const val CATEGORY_ASPECT_RATIO = "Aspect ratio"
        const val CATEGORY_ROTATION     = "Rotation"
        const val CATEGORY_SPEED        = "Speed"



    }

    /** Only IDs are used for equality. */
    override fun equals(other: Any?): Boolean = this === other || (other is Tag && other.id == id)

    /** Only IDs are used for equality. */
    override fun hashCode(): Int = id.hashCode()

    fun isUiContentEqual(other: Tag) = color == other.color && displayName == other.displayName

    fun isLightFontColor() = fontColor?.toLong() == 0xFFFFFFFF
}