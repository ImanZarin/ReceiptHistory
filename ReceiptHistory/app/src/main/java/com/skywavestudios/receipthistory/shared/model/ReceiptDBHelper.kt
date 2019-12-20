package com.skywavestudios.receipthistory.shared.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by zarin on 11/23/2017.
 */
private const val DATABASE_NAME = "MyReceipts.db"
private const val DATABASE_VERSION = 1

class ReceiptDBHelper(val context: Context, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_FACTOR_TABLE = "CREATE TABLE " +
                ReceiptContract.ReceiptEntry.TABLE_NAME + "(" +
                ReceiptContract.ReceiptEntry.COLUMN_ID + " TEXT," +
                ReceiptContract.ReceiptEntry.COLUMN_DATE + " TEXT," +
                ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE + " TEXT," +
                ReceiptContract.ReceiptEntry.COLUMN_ROWSMS + " TEXT," +
                ReceiptContract.ReceiptEntry.COLUMN_TITLE + " TEXT," +
                ReceiptContract.ReceiptEntry.COLUMN_LAT + " DOUBLE," +
                ReceiptContract.ReceiptEntry.COLUMN_LONG + " DOUBLE," +
                ReceiptContract.ReceiptEntry.COLUMN_ACCOUNTNO + " INTEGER," +
                ReceiptContract.ReceiptEntry.COLUMN_PRICE + " DOUBLE," +
                ReceiptContract.ReceiptEntry.COLUMN_ISDEPOSIT + " BIT" +
                ");"
        db.execSQL(SQL_CREATE_FACTOR_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}