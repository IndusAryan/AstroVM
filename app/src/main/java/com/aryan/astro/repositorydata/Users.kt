package com.aryan.astro.repositorydata

data class Users(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: String,
    val phone: String,
    val website: String,
)

data class address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: String,
)

data class geo(
    val lat: String,
    val lng: String,
    )
