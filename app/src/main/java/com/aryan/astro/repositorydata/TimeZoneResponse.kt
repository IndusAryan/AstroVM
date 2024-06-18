package com.aryan.astro.repositorydata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class TimezoneResponse(
    val timezone: String?,
    val city: String
) : Parcelable
