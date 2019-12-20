package com.skywavestudios.receipthistory

import android.app.DialogFragment
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar
import com.skywavestudios.receipthistory.shared.TemplateSmsPatternGroups
import com.skywavestudios.receipthistory.shared.model.Receipt
import kotlinx.android.synthetic.main.fragment_add_transaction.*
import ssc.DateTimeHelper
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import android.widget.ArrayAdapter
import com.skywavestudios.receipthistory.shared.model.ReceiptContract
import kotlinx.android.synthetic.main.activity_receipt.view.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddTransaction_Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddTransaction_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddTransaction_Fragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var date: PersianCalendar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater!!.inflate(R.layout.fragment_add_transaction, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        newtransaction_date.setOnClickListener {
            val persianCalendar = PersianCalendar()
            val datePickerDialog = DatePickerDialog.newInstance(
                    this,
                    persianCalendar.persianYear,
                    persianCalendar.persianMonth,
                    persianCalendar.persianDay
            )
            datePickerDialog.show(fragmentManager, "Datepickerdialog")
        }
        //super.onViewCreated(view, savedInstanceState)

        newtransaction_ok.setOnClickListener(View.OnClickListener { v -> saveReceipt(v) })

        newtransaction_cost.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count != before) {
                    var sTemp = s.toString().replace(",", "")
                    try{
                        var intTemp = Integer.parseInt(sTemp)
                        //var reorderTemp = ConstantsApp.intToViseversa(intTemp)
                        newtransaction_cost.setText(ConstantsApp.Int_To_Price(intTemp.toLong()))
                        newtransaction_cost.setSelection(newtransaction_cost.getText().length)
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                }

            }

            override fun afterTextChanged(s: Editable) {}
        })

        val items = arrayOf(getString(R.string.mode_add), getString(R.string.mode_minus))
        val adapter = ArrayAdapter(ConstantsApp.AppContext, android.R.layout.simple_spinner_dropdown_item, items)
        newtransaction_mode.adapter = adapter

        val bankMap = ConstantsApp.getAllBanks(ConstantsApp.AppContext)
        val bankList: MutableList<String> = mutableListOf()
        for (j in bankMap)
            bankList.add(j.value)
        val adaptor2 = ArrayAdapter(ConstantsApp.AppContext, android.R.layout.simple_spinner_dropdown_item, bankList)
        newtransaction_bank.adapter = adaptor2
    }

    private fun saveReceipt(v: View?) {
        if (newtransaction_accountNO.text.isNotEmpty() && newtransaction_cost.text.isNotEmpty()
                && newtransaction_date.text.isNotEmpty() && newtransaction_title.text.isNotEmpty()) {
            val newR = Receipt()
            newR.AccountNumber = newtransaction_accountNO.text.toString()
            newR.Title = newtransaction_title.text.toString()
            newR.Price = newtransaction_cost.text.toString().replace(",","").toDouble()
            newR.InsertDateIso = DateTimeHelper.toISO(ConstantsApp.DateFromPersian(date?.persianYear, date?.persianMonth, date?.persianDay, 0, 0, 0))
            if (newtransaction_mode.selectedItem.toString() == getString(R.string.mode_add))
                newR.IsDeposit = true
            else
                newR.IsDeposit = false
            val bankList = ConstantsApp.getAllBanks(ConstantsApp.AppContext)
            for (i in bankList)
                if (i.value == newtransaction_bank.selectedItem.toString()) {
                    newR.FinancialInstituteId = i.key
                    break
                }
            var d: java.util.Date = DateTimeHelper.ParseISO(newR.InsertDateIso)
            var d2 = ConstantsApp.DateToPersian(d)
            var raw = newtransaction_bank.selectedItem.toString() + "\n" + newtransaction_mode.selectedItem.toString() + " مبلغ " + ConstantsApp.Int_To_Price(newR.Price.toLong()) + "\n" + " از " + newR.AccountNumber + "\n" + String.format("%d/%d/%d", d2.persianYear, d2.persianMonth, d2.persianDay)
            newR.RawSms = raw
            ConstantsApp.addtoDB(newR, this.activity)
            this.dismiss()

        } else {
            Toast.makeText(ConstantsApp.AppContext, R.string.popup_nullerror, Toast.LENGTH_LONG).show()

        }
    }

    override fun onDateSet(view: com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog, year: Int, month: Int, dayOfMonth: Int) {
        val date2 = String.format(year.toString() + "/" + String.format("%02d", month + 1) + "/" + String.format("%02d", dayOfMonth))
        var date3 = PersianCalendar()
        date3.setPersianDate(year, month, dayOfMonth)
        date = date3
        newtransaction_date.setText(date2)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

    }

    override fun onDetach() {
        (activity as ActivityMain).refresh()
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddTransaction_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String): AddTransaction_Fragment {
            val fragment = AddTransaction_Fragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
