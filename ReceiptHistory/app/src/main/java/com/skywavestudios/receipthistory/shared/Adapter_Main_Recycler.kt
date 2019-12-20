package com.skywavestudios.receipthistory.shared

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.skywavestudios.receipthistory.ActivityMain
import com.skywavestudios.receipthistory.ConstantsApp
import com.skywavestudios.receipthistory.R
import com.skywavestudios.receipthistory.shared.model.ReceiptContract
import kotlinx.android.synthetic.main.row_transactin_list.view.*
import ssc.DateTimeHelper

/**
 * Created by zarin on 11/18/2017.
 */
class Adapter_Main_Recycler(rListener: RowClickListener, cursor: Cursor, main: ActivityMain) : RecyclerView.Adapter<Adapter_Main_Recycler.MainListViewHolder>() {
    private val _rowclicklistener: RowClickListener = rListener
    private val _Curser: Cursor = cursor
    private var _Ids = arrayOfNulls<String>(cursor.count)
    private val _mainActivity: ActivityMain = main

    override fun getItemCount(): Int {
        return _Curser.count
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MainListViewHolder {
        val context = parent?.getContext()
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.row_transactin_list, parent, false)
        return MainListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainListViewHolder?, position: Int) {
        if (!_Curser.moveToPosition(position))
            return
        holder?.bind(position, position % 2 == 1)
    }

    interface RowClickListener {
        //fun onRowclick(view: View, id: Long?)
        fun onRowclick(view: View, recepitID: String)
    }

    inner class MainListViewHolder : RecyclerView.ViewHolder, View.OnClickListener {

        constructor (itemView: View) : super(itemView) {

            itemView?.setOnClickListener(this)
        }

        override fun onClick(p0: View) {
            val position = adapterPosition
            val id = _Ids[position]
            if (id != null)
                _rowclicklistener.onRowclick(p0, id)
        }

        fun bind(position: Int, IsOdd: Boolean) {
            _Ids[position] = _Curser.getString(_Curser.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_ID))
            if (IsOdd)
                itemView.row_total.background = ConstantsApp.AppContext?.getResources()?.getDrawable(R.drawable.rounded_rect_odd)
            else
                itemView.row_total.background = ConstantsApp.AppContext?.getResources()?.getDrawable(R.drawable.rounded_rect_even)
            itemView.row_bank.setImageDrawable(ConstantsApp.AppContext?.getResources()?.getDrawable(ConstantsApp.getBankIcon(_Curser.getString(_Curser.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_INSTITUTE)), _mainActivity)))
            itemView.row_bank.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
            itemView.row_updown.setImageBitmap(ConstantsApp.getStatusIcon(_Curser.getInt(_Curser.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_ISDEPOSIT)) != 0, _mainActivity))
            itemView.row_updown.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
            var d: java.util.Date = DateTimeHelper.ParseISO(_Curser.getString(_Curser.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_DATE)))
            var d2 = ConstantsApp.DateToPersian(d)
            itemView.row_date.text = String.format("%d/%d/%d", d2.persianYear, d2.persianMonth, d2.persianDay)
            var cost = _Curser.getDouble(_Curser.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_PRICE)).toInt()
            val f = cost
            itemView.row_cost.text = ConstantsApp.Int_To_Price(f.toLong())

            if (_Curser.getString(_Curser.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_TITLE)) != null)
                itemView.row_title.text = _Curser.getString(_Curser.getColumnIndex(ReceiptContract.ReceiptEntry.COLUMN_TITLE))
        }
    }
}