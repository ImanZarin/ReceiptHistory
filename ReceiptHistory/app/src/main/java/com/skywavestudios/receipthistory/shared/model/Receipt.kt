package com.skywavestudios.receipthistory.shared.model

import java.util.*

/**
 * Created by Mostafa on 11/17/2017
 */
class Receipt {
    var Id = ""
    var Title = ""
    var FinancialInstituteId = ""
    var IsDeposit = false
    var AccountNumber = ""
    var InsertDateIso = ""
    var RawSms = ""
    var Price = 0.0
    var Lat: Double? = null
    var Long: Double? = null
}