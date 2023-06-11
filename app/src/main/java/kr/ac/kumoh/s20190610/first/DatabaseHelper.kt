package kr.ac.kumoh.s20190610.first

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//data class Product(
//    val id: Int,
//    val productName: String,
//    val category: String,
//    val exp: String,
//    val quantity: Int
//)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "database.db", null, 1) {

    companion object {
        private const val DATABASE_NAME = "Database"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "product"

        // 컬럼명 상수 정의
        private const val COLUMN_ID = "id"
        private const val COLUMN_PRODUCT_NAME = "product_name"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_EXP = "exp"
        private const val COLUMN_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUCT_NAME TEXT,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_EXP DATE,
                $COLUMN_QUANTITY INTEGER
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    fun addProduct(product: MyItem): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, product.product)
            put(COLUMN_CATEGORY, product.type)
            put(COLUMN_EXP, product.expirationDate)
            put(COLUMN_QUANTITY, product.num)
        }
        val id = db.insert(TABLE_NAME, null, contentValues)
        db.close()

        return id
    }

    fun getAllProducts(): ArrayList<MyItem> {
        val productList = ArrayList<MyItem>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COLUMN_ID)
            val productNameIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME)
            val categoryIndex = cursor.getColumnIndex(COLUMN_CATEGORY)
            val expIndex = cursor.getColumnIndex(COLUMN_EXP)
            val quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY)

            do {
                val id = cursor.getLong(idIndex)
                val productName = cursor.getString(productNameIndex)
                val category = cursor.getString(categoryIndex)
                val exp = cursor.getString(expIndex)
                val quantity = cursor.getString(quantityIndex)

                val product = MyItem(id, productName, category, exp, quantity)
                productList.add(product)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return productList
    }

    fun updateProduct(product: MyItem) {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_PRODUCT_NAME, product.product)
            put(COLUMN_CATEGORY, product.type)
            put(COLUMN_EXP, product.expirationDate)
            put(COLUMN_QUANTITY, product.num)
        }
        db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(product.id.toString()))
        db.close()
    }

    fun deleteProduct(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteAllProduct() {
        val db = writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }

}