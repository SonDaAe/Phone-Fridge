package kr.ac.kumoh.s20190610.first

import java.io.Serializable

data class ProductData(
    var productName: String,
    var unitPrice: Int,
    var quantity: Int,
    var price: Int,
    var category: String,
    var exp: Int
) : Serializable
