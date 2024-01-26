package com.aryan.astro.obj

data class AstroApiResponse(
    val Status: String,
    val Payload: Payload
)

data class Payload(
    val MoonSignName: String
)
