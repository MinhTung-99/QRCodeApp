package com.qrcodeapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.qrcodeapp.model.Scan
import java.util.*

class ScanDB(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val TABLE_QR_CODE = "QrCodeScan"

    private val KEY_ID = "id"
    private val KEY_TEXT = "text"
    private val KEY_DATE = "date"
    private val KEY_TIME = "time"
    private val KEY_IMAGE = "image"

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_QR_CODE_TABLE = "CREATE TABLE " + TABLE_QR_CODE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    KEY_TEXT + " TEXT," +
                    KEY_DATE + " TEXT," +
                    KEY_TIME + " TEXT," +
                    KEY_IMAGE + " BLOB)"

        db.execSQL(CREATE_QR_CODE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }


    fun addQrCodeScanDB(scan: Scan) {
        var db: SQLiteDatabase? = null
        try {
            //open connect
            db = this.writableDatabase

            val values = ContentValues().apply {
                put(KEY_TEXT, scan.text)
                put(KEY_DATE, scan.date)
                put(KEY_TIME, scan.time)
                put(KEY_IMAGE, scan.image)
            }

            db.insert(TABLE_QR_CODE, "", values)
        }
        catch (e: Exception) {
            Log.d("KMGH", "${e.message} =")
        }
        finally {
            db!!.close()
        }
    }

    fun getAllQrCodesScan(): ArrayList<Scan> {
        val qrCodesScan = ArrayList<Scan>()
        val selectQuery =
            "SELECT * FROM $TABLE_QR_CODE"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val qrCodeSan = Scan(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getBlob(4)
                )
                qrCodeSan.id = cursor.getInt(0)
                qrCodesScan.add(qrCodeSan)
            } while (cursor.moveToNext())
        }
        return qrCodesScan
    }

    fun deleteScan(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_QR_CODE, KEY_ID + "=?", arrayOf(id.toString())
        )
    }

    fun deleteAllScan(): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_QR_CODE, null, null)
    }

    companion object{
        private val DATABASE_NAME = "QrCodeScanManager"
        private val DATABASE_VERSION = 1
    }
}