package com.skywavestudios.receipthistory

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.google.gson.GsonBuilder
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar
import com.skywavestudios.receipthistory.shared.model.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Mostafa on 11/17/2017
 */
class ConstantsApp {
    companion object {
        val Gson = GsonBuilder().create()
        val TAG = "ReceiptHistory"
        var AppContext: Context? = null
        val DATABASE_VERSION = 1

        fun ResourceReadRaw(context: Context, resId: Int): String {
            val inputStream = context.resources.openRawResource(resId)
            val stringWriter = StringWriter()
            val buffer = CharArray(1024)
            try {
                val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                var n: Int
                while (true) {
                    n = reader.read(buffer)
                    if (n == -1)
                        break
                    stringWriter.write(buffer, 0, n)
                }
            } finally {
                inputStream.close()
            }
            val r = stringWriter.toString()
            return r
        }

        fun ResourceFinancialInstitutes(context: Context): FinancialInstitutes {
            return ConstantsApp.Gson.fromJson(ConstantsApp.ResourceReadRaw(context, R.raw.financial_institutes), FinancialInstitutes::class.java)
        }

        fun DateFromPersian(dYear: Int?, dMonth: Int?, dDay: Int?, dHour: Int?, dMinute: Int?, dSecond: Int?): Date {
            val persianCal = PersianCalendar()
            persianCal.setPersianDate(dYear ?: 0, dMonth ?: 0, dDay ?: 0)
            persianCal.set(Calendar.HOUR_OF_DAY, dHour ?: 0)
            persianCal.set(Calendar.MINUTE, dMinute ?: 0)
            persianCal.set(Calendar.SECOND, dSecond ?: 0)
            return persianCal.time
        }

        fun DateFromPersian(dYear: String?, dMonth: String?, dDay: String?, dHour: String?, dMinute: String?, dSecond: String?): Date {
            //val persianCal = PersianCalendar()
            var sYear = dYear
            if (sYear?.length == 2)
                sYear = "13$sYear"
            return DateFromPersian(sYear?.toIntOrNull()
                    , (dMonth?.toIntOrNull() ?: 1) - 1
                    , dDay?.toIntOrNull()
                    , dHour?.toIntOrNull()
                    , dMinute?.toIntOrNull()
                    , dSecond?.toIntOrNull())
        }

        fun DateToPersian(date: Date): PersianCalendar {
            val cal = PersianCalendar(date.time)
//            var d = Date()
//            d.year = cal.persianYear
//            d.month = cal.persianMonth + 1
//            d.date = cal.persianDay
//            return d
            return cal
        }

        fun getAllReceipts(mDB: SQLiteDatabase): Cursor {
            return mDB.query(
                    ReceiptContract.ReceiptEntry.TABLE_NAME,
                    null, null, null, null, null,
                    ReceiptContract.ReceiptEntry.COLUMN_DATE + " DESC," + ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE + " DESC"
            )

        }

        fun getCardList(mCursor: Cursor, context: Context): Map<String, String> {
            //var r2: MutableList<String> = mutableListOf<String>()
            var r: MutableMap<String, String> = mutableMapOf()
            mCursor.moveToFirst()
            var institues = ConstantsApp.Gson.fromJson(ConstantsApp.ResourceReadRaw(context, R.raw.financial_institutes), FinancialInstitutes::class.java)
            for (j in institues)
                if (j.Id == mCursor.getString(mCursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE)))
                    r.set(j.Id, j.Name)
            while (mCursor.moveToNext()) {
                if (!r.containsKey(mCursor.getString(mCursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE))))
                    for (j in institues)
                        if (j.Id == mCursor.getString(mCursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE)))
                            r.set(j.Id, j.Name)
            }
            return r
        }

        fun getAllBanks(context: Context?): Map<String, String> {
            var r: MutableMap<String, String> = mutableMapOf()
            if (context != null) {
                var institues = ConstantsApp.Gson.fromJson(ConstantsApp.ResourceReadRaw(context, R.raw.financial_institutes), FinancialInstitutes::class.java)
                for (i in institues)
                    r.set(i.Id, i.Name)
            }
            return r
        }

        fun addtoDB(r: Receipt?, context: Context): String {
            var r2 = ""
            if (r != null) {
                val _dbHelper = ReceiptDBHelper(context, DATABASE_VERSION)
                val mDB = _dbHelper.getWritableDatabase()
                r2 = UUID.randomUUID().toString()
                r2 = r2.replace("-", "")
                val contentValues = ContentValues()
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_ACCOUNTNO, r.AccountNumber)
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_DATE, r.InsertDateIso)
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_ID, r2)
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE, r.FinancialInstituteId)
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_PRICE, r.Price)
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_ROWSMS, r.RawSms)
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_TITLE, r.Title)
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_LAT, r?.Lat)
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_LONG, r?.Long)
                if (r.IsDeposit)
                    contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_ISDEPOSIT, 1)
                else
                    contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_ISDEPOSIT, 0)
                mDB.insertOrThrow(ReceiptContract.ReceiptEntry.TABLE_NAME, null, contentValues)

                //TOADD message for succesfully saved
            }

            return r2
        }

        fun savetoDB(r: Receipt?, context: Context) {
            if (r != null) {
                val _dbHelper = ReceiptDBHelper(context, DATABASE_VERSION)
                val mDB = _dbHelper.getWritableDatabase()
                var r3 = getReceipt(r.Id, mDB)
                r3?.Title = r.Title
                val contentValues = ContentValues()
                contentValues.put(ReceiptContract.ReceiptEntry.COLUMN_TITLE, r.Title)

                mDB?.update(ReceiptContract.ReceiptEntry.TABLE_NAME,
                        contentValues,
                        ReceiptContract.ReceiptEntry.COLUMN_ID + "=\"" + r.Id + "\"",
                        null)
            }
        }

        fun getReceipt(id: String, mDB: SQLiteDatabase): Receipt? {
            var r: Receipt? = null
            val columnCondition = ReceiptContract.ReceiptEntry.COLUMN_ID + " LIKE ?"
            var cursor = mDB.query(
                    ReceiptContract.ReceiptEntry.TABLE_NAME,
                    null,
                    columnCondition,
                    arrayOf(id + "%"),
                    null,
                    null,
                    null)
            if (cursor.moveToFirst()) {
                r = Receipt()
                r.InsertDateIso = cursor.getString(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_DATE))
                r.Price = cursor.getDouble(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_PRICE))
                r.Title = cursor.getString(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_TITLE))
                r.AccountNumber = cursor.getString(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_ACCOUNTNO))
                r.FinancialInstituteId = cursor.getString(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE))
                r.Id = cursor.getString(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_ID))
                r.RawSms = cursor.getString(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_ROWSMS))
            }

            return r
        }

        fun getBankIcon(fiId: String, context: Context): Int {
            var b: Int
            when (fiId) {
                "1" -> b = R.drawable.fi_resalat
                "2" -> b = R.drawable.fi_melli
                "3" -> b = R.drawable.fi_post
                "4" -> b = R.drawable.fi_sepah
                "5" -> b = R.drawable.fi_sanat_madan
                "6" -> b = R.drawable.fi_keshavarzi
                "7" -> b = R.drawable.fi_maskan
                "8" -> b = R.drawable.fi_eghtesad_novin
                "9" -> b = R.drawable.fi_parsian
                "10" -> b = R.drawable.fi_karafarin
                "11" -> b = R.drawable.fi_saman
                "12" -> b = R.drawable.fi_pasargard
                "13" -> b = R.drawable.fi_sarmaye
                "14" -> b = R.drawable.fi_sina
                "15" -> b = R.drawable.fi_shahr
                "16" -> b = R.drawable.fi_ansar
                "17" -> b = R.drawable.fi_day
                "18" -> b = R.drawable.fi_tejarat
                "19" -> b = R.drawable.fi_refah
                "20" -> b = R.drawable.fi_saderat
                "21" -> b = R.drawable.fi_melat
                "22" -> b = R.drawable.fi_hekmat
                "23" -> b = R.drawable.fi_gardeshgari
                "24" -> b = R.drawable.fi_iranzamin
                "25" -> b = R.drawable.fi_ghavamin
                "26" -> b = R.drawable.fi_unknown
                "27" -> b = R.drawable.fi_ayandeh
                else -> b = R.drawable.fi_unknown

            }

            return b
        }

        fun getBankName(fiId: String, context: Context): String {

            for (i in getAllBanks(context))
                if (i.key == fiId)
                    return i.value
            return ""
        }

        fun getStatusIcon(isDeposite: Boolean, context: Context): Bitmap {
            var b: Bitmap
            when (isDeposite) {
                false -> b = (context.getResources().getDrawable(R.drawable.minus) as BitmapDrawable).bitmap
                true -> b = (context.getResources().getDrawable(R.drawable.plus) as BitmapDrawable).bitmap
            }
            return b
        }

        fun Int_To_Price(i: Long): String {
            val formatter = DecimalFormat("###,###,###,###,###,###,###,###")
            return formatter.format(i)
        }

        fun removeFromDB(receiptId: String?, context: Context) {
            val _dbHelper = ReceiptDBHelper(context, DATABASE_VERSION)
            val mDB = _dbHelper.getWritableDatabase()
            val ms: Array<String?> = arrayOf(receiptId)
            try{
                mDB.delete(ReceiptContract.ReceiptEntry.TABLE_NAME, ReceiptContract.ReceiptEntry.COLUMN_ID + " =? " , ms)
                mDB.close()
            }catch (e : Exception){
                e.printStackTrace()
            }

        }

        fun intToViseversa(first: Int): Int {
            var l = first.toString().length
            var a = mutableListOf<Int>()
            var temp: Int = first
            for (i in 0..l - 1) {
                val result = 10//Math.pow(10.toDouble(), (i + 1).toDouble())
                a.add(temp % result.toInt())
                temp = (temp - (temp % result.toInt())) / 10
            }
            var newString = ""
            for (j in 0..l - 1)
                newString += a[j]
            return Integer.parseInt(newString)
        }
    }
}