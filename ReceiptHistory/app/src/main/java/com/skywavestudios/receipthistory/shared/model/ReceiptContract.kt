package com.skywavestudios.receipthistory.shared.model

import android.provider.BaseColumns

/**
 * Created by zarin on 11/23/2017.
 */
class ReceiptContract {
    class ReceiptEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "RECEIPTDB"
            val COLUMN_ID = "Id"
            val COLUMN_TITLE = "Title"
            val COLUMN_INSTITUTE = "Institute"
            val COLUMN_ACCOUNTNO = "AccountNO"
            val COLUMN_DATE = "Date"
            val COLUMN_ROWSMS = "Sms"
            val COLUMN_PRICE = "Price"
            val COLUMN_LAT = "Lat"
            val COLUMN_LONG = "Long"
            val COLUMN_ISDEPOSIT = "IsDeposit"
        }
    }
}