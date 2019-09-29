package com.moosphon.g2v

import android.app.Application
import com.moosphon.g2v.engine.image.glide.GlideLoaderStrategy
import com.moosphon.g2v.engine.image.loader.ImageLoader

class G2VideoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ImageLoader.INSTANCE.init(GlideLoaderStrategy())
    }
}