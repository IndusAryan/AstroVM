package com.aryan.astro.repositorydata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoonSignData(
    val moonSign: String
) : Parcelable
