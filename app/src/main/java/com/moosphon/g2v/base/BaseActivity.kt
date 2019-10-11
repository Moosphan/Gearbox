package com.moosphon.g2v.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moosphon.g2v.util.ScreenUtils

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (applyDefaultStatusStyle()) {
            ScreenUtils.applyStatusBarStyle(this)
        }
    }

    abstract fun applyDefaultStatusStyle() : Boolean
}