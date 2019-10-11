package com.moosphon.g2v.page

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.moosphon.g2v.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about_me.*
import android.content.ActivityNotFoundException
import android.content.Intent
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import com.blankj.utilcode.util.ToastUtils
import com.moosphon.g2v.BuildConfig
import com.moosphon.g2v.R
import com.moosphon.g2v.base.Configs
import com.moosphon.g2v.util.applyViewGone
import com.moosphon.g2v.util.navigateTo
import java.net.URLEncoder


class AboutMeActivity : BaseActivity() {
    override fun applyDefaultStatusStyle(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_me)
        initialize()
    }

    private fun initialize() {

        val version = BuildConfig.VERSION_NAME
        aboutAppVersion.text = String.format(getString(R.string.version_holder), version)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        aboutAppFeedback.setOnClickListener {
            giveSuggestion()
        }

        aboutAppDevPlan.setOnClickListener {
            displayDialog(DEVELOP_PLAN_TYPE)
        }

        aboutAppOpenSource.setOnClickListener {
            navigateTo<OpenSourceLicenseActivity>()
        }

        aboutAppDonate.setOnClickListener {
            if (hasInstalledAlipayClient(this)) {
                startDonation(this, Configs.PAY_URL)
            } else {
                ToastUtils.showShort("尚未安装支付宝呢，下次再捐助吧≡ω≡")
            }
        }

        aboutAppAboutMe.setOnClickListener {
            displayDialog(USER_DETAIL_TYPE)
        }

        aboutAppContactMe.setOnClickListener {
            aboutAppContactMe.text = getString(R.string.contact_me_email_info)
            Handler().postDelayed({
                aboutAppContactMe.text = getString(R.string.contact_me)
            },
            2000)
        }

    }

    private fun displayDialog(type: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_common, null, false)
        val titleText = view.findViewById<TextView>(R.id.common_dialog_title)
        val contentText = view.findViewById<TextView>(R.id.common_dialog_content)
        val topIcon = view.findViewById<ImageView>(R.id.common_dialog_ic)
        val clipBox = view.findViewById<LinearLayout>(R.id.common_dialog_clip_box)
        val dialog = Dialog(this, R.style.DialogTransparent)
        val lp = FrameLayout.LayoutParams(
            ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(64f),
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        if (type == USER_DETAIL_TYPE) {
            titleText.text = getString(R.string.author_name)
            contentText.text = getString(R.string.author_desc)
            topIcon.setImageResource(R.drawable.ic_avatar)
            clipBox.applyViewGone(false)
        } else if (type == DEVELOP_PLAN_TYPE) {
            clipBox.applyViewGone(true)
            titleText.text = getString(R.string.develop_plan_title)
            contentText.text = getString(R.string.develop_plan_content)
            topIcon.setImageResource(R.drawable.ic_plan)
        }
        dialog.setContentView(view, lp)
        dialog.show()
    }

    //检查支付宝是否安装
    private fun hasInstalledAlipayClient(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            val info = pm.getPackageInfo(Configs.ALI_PAY_PACKAGE_NAME, 0)
            info != null
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }


    private fun startDonation(activity: Activity, urlCode: String): Boolean {
        return startDonationIntent(activity, doFormUri(urlCode))
    }

    private fun doFormUri(urlCode: String): String {
        var urlEncode = urlCode
        try {
            urlEncode = URLEncoder.encode(urlCode, "utf-8")
        } catch (e: Exception) {
        }

        val alipayqr =
            "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=$urlEncode"
        return alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis()
    }


    private fun startDonationIntent(activity: Activity, intentFullUrl: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intentFullUrl))
            activity.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    private fun giveSuggestion(
        subject: String = Configs.EMAIL_DEFAULT_SUBJECT,
        content: String = Configs.EMAIL_DEFAULT_CONTENT) {

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(Configs.EMAIL_ADDRESS))
        if (subject.isNotEmpty())
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (content.isNotEmpty())
            intent.putExtra(Intent.EXTRA_TEXT, content)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }


    companion object {
        const val USER_DETAIL_TYPE = 0
        const val DEVELOP_PLAN_TYPE = 1
    }


}
