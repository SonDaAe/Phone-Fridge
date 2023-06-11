package kr.ac.kumoh.s20190610.first

import android.content.Context
import android.database.sqlite.SQLiteDatabase

object DatabaseUtil {
    private lateinit var database: SQLiteDatabase

    fun init(context: Context) {
        val dbHelper = DatabaseHelper(context)
        database = dbHelper.writableDatabase
    }

    fun getDatabase(): SQLiteDatabase {
        return database
    }
}