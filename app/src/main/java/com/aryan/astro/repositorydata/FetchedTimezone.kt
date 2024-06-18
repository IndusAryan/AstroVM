package com.aryan.astro.repositorydata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FetchedTimezone(
    val userTimezone: String? = null
) : Parcelable
