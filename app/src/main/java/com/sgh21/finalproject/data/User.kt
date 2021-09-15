package com.sgh21.finalproject.data

import java.io.Serializable

data class User(
    var id: String? = null,
    var urlPicture: String? = null,
    var name: String? = null,
    var lastName: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var idsFavorites: MutableList<String>? = null
) : Serializable
