package com.qrcodeapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.qrcodeapp.model.QRCode
import java.util.*

class QRCodeDB(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val TABLE_QR_CODE = "QrCode"
    private val KEY_ID = "id"
    private val KEY_TEXT = "text"
    private val KEY_TYPE_QR = "typeQR"
    private val KEY_IMAGE = "image"

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_QR_CODE_TABLE = "CREATE TABLE " + TABLE_QR_CODE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_TEXT + " TEXT," +
                    KEY_TYPE_QR + " TEXT," +
                    KEY_IMAGE + " BLOB)"

        db.execSQL(CREATE_QR_CODE_TABLE)
    }

    fun addQrCode(qrCode: QRCode) {
        var db: SQLiteDatabase? = null
        try {
            //mo ket noi
            db = this.writableDatabase
            val values = ContentValues().apply {
                put(KEY_TEXT, qrCode.text)
                put(KEY_TYPE_QR, qrCode.typeQR)
                put(KEY_IMAGE, qrCode.image)
            }

            db.insert(TABLE_QR_CODE, "", values)
        } catch (e: Exception) {
        } finally {
            db!!.close()
        }
    }

    fun getAllQrCodes(): ArrayList<QRCode> {
        val qrCodes = ArrayList<QRCode>()
        val selectQuery =
            "SELECT * FROM $TABLE_QR_CODE"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val qrCode = QRCode(cursor.getString(1), cursor.getString(2), cursor.getBlob(3))
                qrCode.id = cursor.getInt(0)
                qrCodes.add(qrCode)
            } while (cursor.moveToNext())
        }
        return qrCodes
    }

    fun deleteQrCode(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_QR_CODE, "$KEY_ID=?", arrayOf(id.toString())
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    companion object{
        private val DATABASE_NAME = "QrCodeManager"
        private val DATABASE_VERSION = 1
    }
}