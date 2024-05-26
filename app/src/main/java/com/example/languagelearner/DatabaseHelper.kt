package com.example.languagelearner

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_EMAIL TEXT," +
                "$COLUMN_PASSWORD TEXT," +
                "$COLUMN_PHONE TEXT)")
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)

    }

    fun insertUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, user.name)
        values.put(COLUMN_EMAIL, user.email)
        values.put(COLUMN_PASSWORD, user.password)
        values.put(COLUMN_PHONE, user.phone)



        val result = db.insert(TABLE_NAME, null, values)
        return result
    }

//    fun getUser(email: String): User? {
//        val db = this.readableDatabase
//        val cursor = db.query(
//            TABLE_NAME, arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_PHONE),
//            "$COLUMN_EMAIL=?", arrayOf(email), null, null, null)
//        cursor?.let {
//            if (it.moveToFirst()) {
//                val user = User(
//                    id = it.getInt(it.getColumnIndex(COLUMN_ID)),
//                    name = it.getString(it.getColumnIndex(COLUMN_NAME)),
//                    email = it.getString(it.getColumnIndex(COLUMN_EMAIL)),
//                    password = it.getString(it.getColumnIndex(COLUMN_PASSWORD)),
//                    phone = it.getString(it.getColumnIndex(COLUMN_PHONE))
//                )
//                cursor.close()
//                return user
//            }
//        }
//        cursor.close()
//        return null
//    }

    fun readUser(user: User) : Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val selectionArguments = arrayOf(user.name, user.email, user.phone, user.password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArguments, null, null, null)

        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    companion object{
        private const val DATABASE_NAME = "UserDb.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_PHONE = "phone"



    }
}