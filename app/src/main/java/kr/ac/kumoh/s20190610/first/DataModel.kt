package kr.ac.kumoh.s20190610.first

import java.io.Serializable

data class ProductData(
    val productName: String,
    val unitPrice: Int,
    val quantity: Int,
    val price: Int,
    val category: String,
    val exp: Int
) : Serializable