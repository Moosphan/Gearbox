package com.moosphon.g2v.base

import android.os.Environment

object Configs {

    val APP_DIR = Environment.getExternalStorageDirectory().toString() + "/G2Video/"
    const val GIF_DIR = "gifs/"
    const val VIDEO_DIR = "video/"

    const val GITHUB_OF_MYSELF = "https://github.com/Moosphan"
    const val GITHUB_OF_PROJECT = "https://github.com/Moosphan/G2Video"
    const val ISSUE_URL = "https://github.com/Moosphan/G2Video/issues/new"
    const val PAY_URL = "HTTPS://QR.ALIPAY.COM/FKX08495CC5DMFZG3PCOC1"
    const val ALI_PAY_PACKAGE_NAME = "com.eg.android.AlipayGphone"
    const val EMAIL_ADDRESS = "moosphon@gmail.com"
    const val EMAIL_DEFAULT_SUBJECT = "建议与反馈"
    const val EMAIL_DEFAULT_CONTENT = ""

}