package com.moosphon.g2v.page

import android.os.Bundle
import com.moosphon.g2v.R
import com.moosphon.g2v.base.BaseActivity
import kotlinx.android.synthetic.main.activity_open_source_license.*

class OpenSourceLicenseActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_source_license)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun applyDefaultStatusStyle(): Boolean = true
}
