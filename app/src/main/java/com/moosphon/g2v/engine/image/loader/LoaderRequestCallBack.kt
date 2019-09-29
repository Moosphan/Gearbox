package com.moosphon.g2v.engine.image.loader


/**
 * @author Moosphon
 *
 * 图片加载结果的回调监听
 */
interface LoaderRequestCallBack {
    fun onSuccess()
    fun onFailed()
}