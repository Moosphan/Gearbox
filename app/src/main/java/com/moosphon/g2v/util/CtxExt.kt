package com.moosphon.g2v.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * Extension for [Context].
 */

/**
 * start activity without params.
 */
inline fun <reified T : Activity> Context.navigateTo() {
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

/**
 * navigate to another activity with params.
 */
inline fun <reified T : Activity> Context.navigateTo(vararg params: Pair<String, Any?>) {
    val intent = createIntent(this, T::class.java, params)
    startActivity(intent)
}

/**
 * generate an intent for activity.
 */
inline fun <reified T : Activity> Context.intentTo() : Intent {
    return Intent(this, T::class.java)
}

fun <T> createIntent(ctx: Context, clazz: Class<out T>, params: Array<out Pair<String, Any?>>): Intent {
    val intent = Intent(ctx, clazz)
    if (params.isNotEmpty()) createIntentArguments(intent, params)
    return intent
}

/**
 * generate params of intent wanted to send to another activity.
 */
private fun createIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
    params.forEach {
        when (val value = it.second) {
            null -> intent.putExtra(it.first, null as java.io.Serializable?)
            is Int -> intent.putExtra(it.first, value)
            is Long -> intent.putExtra(it.first, value)
            is CharSequence -> intent.putExtra(it.first, value)
            is String -> intent.putExtra(it.first, value)
            is Float -> intent.putExtra(it.first, value)
            is Double -> intent.putExtra(it.first, value)
            is Char -> intent.putExtra(it.first, value)
            is Short -> intent.putExtra(it.first, value)
            is Boolean -> intent.putExtra(it.first, value)
            is java.io.Serializable -> intent.putExtra(it.first, value)
            is Bundle -> intent.putExtra(it.first, value)
            is Parcelable -> intent.putExtra(it.first, value)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                else -> throw IllegalArgumentException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            is IntArray -> intent.putExtra(it.first, value)
            is LongArray -> intent.putExtra(it.first, value)
            is FloatArray -> intent.putExtra(it.first, value)
            is DoubleArray -> intent.putExtra(it.first, value)
            is CharArray -> intent.putExtra(it.first, value)
            is ShortArray -> intent.putExtra(it.first, value)
            is BooleanArray -> intent.putExtra(it.first, value)
            else -> throw IllegalArgumentException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
        return@forEach
    }
}


/**
 * get color by brief style
 */
fun Context.getColorResource(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}


