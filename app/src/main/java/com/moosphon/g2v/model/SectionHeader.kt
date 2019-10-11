package com.moosphon.g2v.model

import androidx.annotation.StringRes

data class SectionHeader(
    @StringRes val titleId: Int,
    val useHorizontalPadding: Boolean = true
)