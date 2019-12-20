package com.skywavestudios.receipthistory


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.recyclerview.R.attr.layoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.skywavestudios.receipthistory.shared.ActivityReceipt
import com.skywavestudios.receipthistory.shared.Adapter_Main_Recycler
import com.skywavestudios.receipthistory.shared.Analyzer
import com.skywavestudios.receipthistory.shared.model.Receipt
import com.skywavestudios.receipthistory.shared.model.ReceiptContract
import com.skywavestudios.receipthistory.shared.model.ReceiptDBHelper
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_activity_main.*
import java.security.AccessController.getContext
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class ActivityMain : AppCompatActivity(), Adapter_Main_Recycler.RowClickListener {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var listAdapter: Adapter_Main_Recycler
    public lateinit var mDB: SQLiteDatabase
    lateinit var _dbHelper: ReceiptDBHelper
    lateinit var mCursor: Cursor
    var _cards: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConstantsApp.AppContext = applicationContext
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("font/iran_sans.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build())
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        recyclerView = main_recycler
        fab.setOnClickListener { view ->
            showAddPopup()
        }

        var permissioncheck2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)
        if (permissioncheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS), 1002)
            return
        }
        var permissioncheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)
        if (permissioncheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS), 1003)
            return
        }


        initialize()

    }

    fun initialize() {
        layoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setHasFixedSize(true)
        _dbHelper = ReceiptDBHelper(this, ConstantsApp.DATABASE_VERSION)
        mDB = _dbHelper.getWritableDatabase()
        mCursor = ConstantsApp.getAllReceipts(mDB)
        if (mCursor.count == 0) {
            val sms1 = getFirstSMS()
            if (ConstantsApp.addtoDB(sms1, this).isNotEmpty())
                mCursor = ConstantsApp.getAllReceipts(mDB)
        }
        listAdapter = Adapter_Main_Recycler(this, mCursor, this)
        recyclerView.adapter = listAdapter


        total_earn.text = getString(R.string.total_earn) + " : " + ConstantsApp.Int_To_Price(getTotals(mCursor).first.toLong())
        total_paid.text = getString(R.string.total_paid) + " : " + ConstantsApp.Int_To_Price(getTotals(mCursor).second.toLong())

    }

    override fun onResume() {
        if (::mDB.isInitialized)
            refresh()
        super.onResume()
    }


    override fun onRowclick(view: View, recepitID: String) {
        val i = Intent(this, ActivityReceipt::class.java)
        i.putExtra("receipt", recepitID)
        startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear()
        menu.add(getString(R.string.menu_all))
        if (::mCursor.isInitialized)
            if (mCursor.count > 0)
                for (i in ConstantsApp.getCardList(mCursor, this)) menu.add(1, i.key.toInt(), 1, i.value)
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        var r = false
        if (item.title == getString(R.string.menu_all) || item == null) {
            mCursor = ConstantsApp.getAllReceipts(mDB)
            r = true
        } else if (item.title.isNotEmpty()) {
            mCursor = filterList(item.itemId.toString())
            r = true
        }
        //    else -> super.onOptionsItemSelected(item)
        listAdapter = Adapter_Main_Recycler(this, mCursor, this)
        recyclerView.adapter = listAdapter
        total_earn.text = getString(R.string.total_earn) + " : " + ConstantsApp.Int_To_Price(getTotals(mCursor).first.toLong())
        total_paid.text = getString(R.string.total_paid) + " : " + ConstantsApp.Int_To_Price(getTotals(mCursor).second.toLong())
        return r
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        initialize()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun filterList(card: String): Cursor {
        val columnCondition = ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE + " LIKE ?"
        return mDB.query(
                ReceiptContract.ReceiptEntry.TABLE_NAME,
                null,
                columnCondition,
                arrayOf(card), null, null,
                ReceiptContract.ReceiptEntry.COLUMN_DATE + " DESC"
        )
    }

    fun showAddPopup() {
        val fm = fragmentManager
        val editNameDialogFragment = AddTransaction_Fragment.newInstance("Some Title")
        editNameDialogFragment.show(fm, "fragment_add_transaction")
    }

    fun getFirstSMS(): Receipt? {
        var r: Receipt? = null
        val projection = arrayOf("address", "body")
        val mSmsinboxQueryUri = Uri.parse("content://sms/inbox")
        val cursor = contentResolver.query(mSmsinboxQueryUri, projection, null, null, null)
        if (cursor != null)
            if (cursor!!.moveToFirst()) { // must check the result to prevent exception
                do {
                    var msgData = ""
                    for (idx in 0 until cursor.columnCount) {
                        msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx)
                    }
                    var a = Analyzer(this)
                    r = a.AnalyzeSms(msgData)
                    // use msgData
                } while (r == null && cursor.moveToNext())
            }
        return r
    }

    public fun refresh() {

        mCursor = ConstantsApp.getAllReceipts(mDB)
        listAdapter = Adapter_Main_Recycler(this, mCursor, this)
        recyclerView.adapter = listAdapter
        total_earn.text = getString(R.string.total_earn) + " : " + ConstantsApp.Int_To_Price(getTotals(mCursor).first.toLong())
        total_paid.text = getString(R.string.total_paid) + " : " + ConstantsApp.Int_To_Price(getTotals(mCursor).second.toLong())
    }

    fun getTotals(cursor: Cursor): Pair<Int, Int> {
        var r1 = 0
        var r2 = 0
        cursor.moveToFirst()
        if (cursor.getInt(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_ISDEPOSIT)) != 0)
            r1 += cursor.getDouble(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_PRICE)).toInt()
        else
            r2 += cursor.getDouble(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_PRICE)).toInt()
        while (cursor.moveToNext())
            if (cursor.getInt(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_ISDEPOSIT)) != 0)
                r1 += cursor.getDouble(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_PRICE)).toInt()
            else
                r2 += cursor.getDouble(cursor.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_PRICE)).toInt()
        return Pair(r1, r2)
    }
}
