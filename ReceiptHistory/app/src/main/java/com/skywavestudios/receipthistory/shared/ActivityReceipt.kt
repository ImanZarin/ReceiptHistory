package com.skywavestudios.receipthistory.shared

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.skywavestudios.receipthistory.ConstantsApp
import com.skywavestudios.receipthistory.R
import com.skywavestudios.receipthistory.shared.model.Receipt
import com.skywavestudios.receipthistory.shared.model.ReceiptDBHelper
import kotlinx.android.synthetic.main.activity_receipt.*

class ActivityReceipt : AppCompatActivity() {
    var receipt: Receipt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var _dbHelper = ReceiptDBHelper(this, ConstantsApp.DATABASE_VERSION)
        var mDB = _dbHelper.getWritableDatabase()
        setContentView(R.layout.activity_receipt)
        val receiptId = intent.extras.getString("receipt")
        receipt = ConstantsApp.getReceipt(receiptId, mDB)
        receipt_text.text = receipt?.RawSms
        receipt_title.setText(receipt?.Title)
        receipt_confirm.setOnClickListener(View.OnClickListener { v -> confirmName(v) })
        receipt_remove.setOnClickListener(View.OnClickListener { v -> remove(v, receiptId) })
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)

    }

    fun confirmName(v: View?) {
        receipt?.Title = receipt_title.text.toString()
        ConstantsApp.savetoDB(receipt, this)
        onBackPressed()
    }

    fun remove(v: View, receiptId: String) {
        onBackPressed()
        ConstantsApp.removeFromDB(receiptId, this)
    }
}
