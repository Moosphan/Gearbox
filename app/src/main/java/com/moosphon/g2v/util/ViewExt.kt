package com.moosphon.g2v.util

import android.graphics.Rect
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.internal.ViewUtils.requestApplyInsetsWhenAttached
import com.google.android.material.snackbar.Snackbar

/**
 * set visibility state for [View].
 */
fun View.applyViewGone(isGone: Boolean) {
    this.visibility = if (isGone) View.GONE else View.VISIBLE
}

/**
 * show snack bar on views.
 */
fun View.showSnackBar(message: String, duration: Int) {
    Snackbar.make(this, message, duration).show()
}


/**
 * Inflate a [View] with given layoutId and attach it to the calling [ViewGroup].
 * @param layout Id of the layout to inflate.
 */
fun ViewGroup.inflateView(@LayoutRes layout: Int): View {
    return LayoutInflater.from(this.context).inflate(layout, this, false)
}

/**
 * get input content of [EditText].
 */
fun EditText.obtainTextContent() = this.text.toString().trim()

/**
 * get text content of [TextView].
 */
fun TextView.obtainTextContent() = this.text.toString().trim()

/**
 * let view padding with one direction only.
 */
fun View.padTop(top: Int) {
    setPaddingRelative(
        paddingStart,
        top,
        paddingEnd,
        paddingBottom
    )
}

fun View.padStart(start: Int) {
    setPaddingRelative(
        start,
        paddingTop,
        paddingEnd,
        paddingBottom
    )
}

fun View.padEnd(end: Int) {
    setPaddingRelative(
        paddingStart,
        paddingTop,
        end,
        paddingBottom
    )
}

fun View.padBottom(bottom: Int) {
    setPaddingRelative(
        paddingStart,
        paddingTop,
        paddingEnd,
        bottom
    )
}

/**
 * help to handle window insets of view.
 */
fun View.doOnApplyWindowInsets(f: (View, WindowInsetsCompat, ViewPaddingState) -> Unit) {
    // Create a snapshot of the view's padding state
    val paddingState = createStateForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, paddingState)
        insets
    }
    requestApplyInsetsWhenAttached()
}

/**
 * Call [View.requestApplyInsets] in a safe away. If we're attached it calls it straight-away.
 * If not it sets an [View.OnAttachStateChangeListener] and waits to be attached before calling
 * [View.requestApplyInsets].
 */
fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

private fun createStateForView(view: View) = ViewPaddingState(view.paddingLeft,
    view.paddingTop, view.paddingRight, view.paddingBottom, view.paddingStart, view.paddingEnd)

data class ViewPaddingState(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val start: Int,
    val end: Int
)

fun View.isRtl() = layoutDirection == View.LAYOUT_DIRECTION_RTL

/**
 * Calculated the widest line in a [StaticLayout].
 */
fun StaticLayout.textWidth(): Int {
    var width = 0f
    for (i in 0 until lineCount) {
        width = width.coerceAtLeast(getLineWidth(i))
    }
    return width.toInt()
}

fun newStaticLayout(
    source: CharSequence,
    paint: TextPaint,
    width: Int,
    alignment: Layout.Alignment,
    spacingmult: Float,
    spacingadd: Float,
    includepad: Boolean
): StaticLayout {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        StaticLayout.Builder.obtain(source, 0, source.length, paint, width).apply {
            setAlignment(alignment)
            setLineSpacing(spacingadd, spacingmult)
            setIncludePad(includepad)
        }.build()
    } else {
        @Suppress("DEPRECATION")
        StaticLayout(source, paint, width, alignment, spacingmult, spacingadd, includepad)
    }
}

/**
 * Linearly interpolate between two values.
 */
fun lerp(a: Float, b: Float, t: Float): Float {
    return a + (b - a) * t
}