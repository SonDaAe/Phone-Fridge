package kr.ac.kumoh.s20190610.first

data class MyItem(val id: Long, val type: String, val product: String, val expirationDate: String, var num: String) {
    var updatedCount: Int = 0

}