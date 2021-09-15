package com.sgh21.finalproject.data

import java.io.Serializable

data class Products(
    var id: String? = null,
    var sellerId: String? = null,
    var sellerName: String? = null,
    var sellerLastName: String? = null,
    var sellerPhone: String? = null,
    var sellerEmail: String? = null,
    var urlPictures: MutableList<String>? = null,
    var name: String? = null,
    var price: String? = null,
    var features: String? = null,
    var description: String? = null,
    var questions: MutableList<String>? = null,
    var answer: MutableList<String>? = null
): Serializable
