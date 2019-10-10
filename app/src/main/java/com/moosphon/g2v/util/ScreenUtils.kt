package com.moosphon.g2v.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import com.moosphon.g2v.R


/**
 * A util for obtaining information of screen and providing tools to config screens.
 */
class ScreenUtils private constructor(){
    companion object {

        fun getScreenWidth(ctx: Context): Int = ctx.resources.displayMetrics.widthPixels

        fun getScreenHeight(ctx: Context): Int = ctx.resources.displayMetrics.heightPixels


        /**
         * get action bar height
         */
        fun getActionBarSize(context: Context): Int {
            val value = TypedValue()
            context.theme.resolveAttribute(android.R.attr.actionBarSize, value, true)
            return TypedValue.complexToDimensionPixelSize(
                value.data, context.resources.displayMetrics
            )
        }

        /**
         * obtain the height of status bar.
         */
        fun getStatusBarHeight(ctx: Context): Int {
            val resourceId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                ctx.resources.getDimensionPixelOffset(resourceId)
            } else 0
        }

        /**
         * obtain the height of navigation bar.
         */
        fun getNavigationBarHeight(ctx: Context): Int {
            val resourceId = ctx.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                ctx.resources.getDimensionPixelOffset(resourceId)
            } else 0
        }



        fun setLightStatusBar(view: View) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                view.systemUiVisibility = flags
            }
        }

        fun applyStatusBarStyle(activity: Activity, isDark: Boolean = true) {
            var flags = activity.window.decorView.systemUiVisibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                if (isDark) {
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
            activity.window.decorView.systemUiVisibility = flags
        }



    }
}