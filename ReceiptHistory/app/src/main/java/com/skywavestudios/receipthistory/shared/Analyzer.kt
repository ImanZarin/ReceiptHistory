package com.skywavestudios.receipthistory.shared

import com.skywavestudios.receipthistory.shared.model.Templates
import android.content.Context
import android.util.Log
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar
import com.skywavestudios.receipthistory.ConstantsApp
import com.skywavestudios.receipthistory.R
import com.skywavestudios.receipthistory.shared.model.FinancialInstitutes
import com.skywavestudios.receipthistory.shared.model.Receipt
import ssc.DateTimeHelper
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Mostafa on 11/17/2017
 */
class Analyzer(val context: Context) {
    var templates = Templates()
    var financialInstitutes = FinancialInstitutes()

    init {
        financialInstitutes = ConstantsApp.ResourceFinancialInstitutes(context)
        TemplatesUpdate()
    }

    fun TemplatesUpdate() {
        templates = ConstantsApp.Gson.fromJson(ConstantsApp.ResourceReadRaw(context, R.raw.templates), Templates::class.java)
    }

    fun AnalyzeSms(content: String): Receipt? {
        var r: Receipt? = null
        val now = Date()
        val nowPersianYear = ConstantsApp.DateToPersian(now).persianYear.toString()
        for (feTemplate in templates.SMS) {
            val pattern = feTemplate.Pattern.format(*feTemplate.PatternParams)
            val regex = Regex(pattern, setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
            val matchResult = regex.matchEntire(content)
            if (matchResult != null) {
                r = Receipt()
                r.Id = UUID.randomUUID().toString()
                r.FinancialInstituteId = feTemplate.FinancialInstituteId
                r.IsDeposit = feTemplate.IsDeposit
                r.Price = matchResult.groups[feTemplate.PatternGroupsOrdered.indexOf(TemplateSmsPatternGroups.price.name)]?.value?.replace(",", "", true)?.toDoubleOrNull() ?: 0.0
                r.AccountNumber = matchResult.groups[feTemplate.PatternGroupsOrdered.indexOf(TemplateSmsPatternGroups.fiAccount.name)]?.value ?: ""
                r.InsertDateIso = DateTimeHelper.toISO(ConstantsApp.DateFromPersian(
                        if (feTemplate.PatternGroupsOrdered.contains(TemplateSmsPatternGroups.dYear.name)) matchResult.groups[feTemplate.PatternGroupsOrdered.indexOf(TemplateSmsPatternGroups.dYear.name)]?.value else nowPersianYear
                        , matchResult.groups[feTemplate.PatternGroupsOrdered.indexOf(TemplateSmsPatternGroups.dMonth.name)]?.value
                        , matchResult.groups[feTemplate.PatternGroupsOrdered.indexOf(TemplateSmsPatternGroups.dDay.name)]?.value
                        , matchResult.groups[feTemplate.PatternGroupsOrdered.indexOf(TemplateSmsPatternGroups.dHour.name)]?.value
                        , matchResult.groups[feTemplate.PatternGroupsOrdered.indexOf(TemplateSmsPatternGroups.dMinute.name)]?.value
                        , if (feTemplate.PatternGroupsOrdered.contains(TemplateSmsPatternGroups.dSecond.name)) matchResult.groups[feTemplate.PatternGroupsOrdered.indexOf(TemplateSmsPatternGroups.dSecond.name)]?.value else "0"))
                r.RawSms = content

                val fi = financialInstitutes.firstOrNull { a1 -> a1.Id == r.FinancialInstituteId }
                System.out.println("${fi?.Name}, ${r.IsDeposit}, ${r.AccountNumber}, ${r.Price}, ${r.InsertDateIso}")
                System.out.println(ConstantsApp.Gson.toJson(r))
                break
            }
        }
        if (r == null)
            System.out.println("Match Failed")
        return r
    }
}