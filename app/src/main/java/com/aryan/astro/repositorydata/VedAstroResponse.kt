package com.aryan.astro.repositorydata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class AstroApiResponse(
    val Status: String,
    val Payload: Payload
): Parcelable

@Parcelize
@Serializable
data class Payload(
    val MoonSignName: String
) : Parcelable
