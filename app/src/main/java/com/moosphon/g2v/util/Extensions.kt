package com.moosphon.g2v.util

import android.os.Parcel
import android.util.Log
import androidx.core.os.ParcelCompat


fun loge(info: String) {
    Log.e(APP_TAG, info)
}

const val APP_TAG = "G2Video"

// region Parcelables, Bundles

/** Write a boolean to a Parcel. */
fun Parcel.writeBooleanUsingCompat(value: Boolean) = ParcelCompat.writeBoolean(this, value)

/** Read a boolean from a Parcel. */
fun Parcel.readBooleanUsingCompat() = ParcelCompat.readBoolean(this)

// endregion