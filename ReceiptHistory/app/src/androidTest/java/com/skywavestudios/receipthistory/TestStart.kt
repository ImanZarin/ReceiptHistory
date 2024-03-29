package com.skywavestudios.receipthistory

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.skywavestudios.receipthistory.shared.Analyzer

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestStart {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.skywavestudios.receipthistory", appContext.packageName)
    }

    @Test
    fun analyzerSms() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        val analyzer = Analyzer(appContext)
        for (feSample in smsSamples) {
            analyzer.AnalyzeSms(feSample)
        }
        assertEquals("com.skywavestudios.receipthistory", appContext.packageName)
    }

    val smsSamples = arrayListOf(
            "\n" +
                    "*بانک قرض الحسنه رسالت*\n" +
                    "کارت\n" +
                    "برداشت از: 10.5327048.1\n" +
                    "مبلغ: 109,000 ريال\n" +
                    "96/08/26_12:56\n" +
                    "موجودي: 626,296,501 ريال"

            , "*بانک قرض الحسنه رسالت*\n" +
            "کارت\n" +
            "واريز به: 10.5327048.1\n" +
            "مبلغ: 5,100,000 ريال\n" +
            "96/08/27_20:28\n" +
            "موجودي: 631,236,501 ريال"

            , "بانک اقتصادنوين\n" +
            "برداشت از: 141-800-140467-1 \n" +
            "12,000 ريال\n" +
            "1396/8/27\n" +
            "16:26\n" +
            "مانده:73,925 ريال"

            , "بانک اقتصادنوين\n" +
            "واريز به:  164-1-140467-1 \n" +
            "12,000 ريال\n" +
            "1396/8/27\n" +
            "16:26 \n" +
            "مانده: 20,000,000 ريال"

            , "بانک ملت\n" +
            "حواله پايا\n" +
            "برداشت از حساب 5790656935\n" +
            "مبلغ : 40,000,000 ريال\n" +
            "موجودي : 379,891 ريال\n" +
            "مستند : 1963898260\n" +
            "96/07/19 01:01"

            , "بانک ملت\n" +
            "حواله اينترنتي\n" +
            "واريز به حساب 5790656935\n" +
            "مبلغ : 40,000,000 ريال\n" +
            "موجودي : 40,379,891 ريال\n" +
            "مستند : 1273831376\n" +
            "96/07/18 18:04"

            , "*بانك پاسارگاد*\n" +
            "کارت\n" +
            "برداشت از: 372.8000.12831187.1\n" +
            "مبلغ: 152,000 ريال\n" +
            "96/08/26_11:20\n" +
            "موجودي: 729,065 ريال"

            , "*بانك پاسارگاد*\n" +
            "اتاق پاياپاي الکترونيک\n" +
            "واريز به: 372.8000.12831187.1\n" +
            "مبلغ: 760,000 ريال\n" +
            "96/08/25_20:11\n" +
            "موجودي: 881,065 ريال"

            , "بانك سامان\n" +
            "برداشت مبلغ 200,000 ازخود پرداز\n" +
            "از  859-800-2018242-1 \n" +
            "مانده 27,297\n" +
            "1395/12/28\n" +
            "17:26:39"

            , "بانك سامان\n" +
            "واريز مبلغ  250,000ريال\n" +
            "به  859-800-2018242-1 \n" +
            "مانده 277,297\n" +
            "1396/8/3\n" +
            "16:07"

            , "*بانك گردشگری*\n" +
            "کارت\n" +
            "برداشت از: 110.9980.424062.1\n" +
            "مبلغ: 350,000 ريال\n" +
            "96/08/25_21:23\n" +
            "موجودي: 302,310,127 ريال"

            , "*بانك گردشگری*\n" +
            "کارت\n" +
            "واريز به: 110.9980.424062.1\n" +
            "مبلغ: 240,000 ريال\n" +
            "96/08/23_21:37\n" +
            "موجودي: 304,153,127 ريال"
            ,
            "بانک صادرات\n" +
                    "انتقال+کارمزد:150,033,000-\n" +
                    "حساب:49007\n" +
                    "مانده:8,116,909\n" +
                    "0616-10:48"
            ,
            "بانک صادرات\n" +
                    "سود:124,538+\n" +
                    "حساب:49007\n" +
                    "مانده:218,218\n" +
                    "0328-24:00"
            ,
            "بانک صادرات\n" +
                    "برداشت:250,000-\n" +
                    "حساب:49007\n" +
                    "مانده:68,869,680\n" +
                    "0315-10:46"
            ,
            "بانک صادرات\n" +
                    "کارمزد پيامك:100,000-\n" +
                    "حساب:49007\n" +
                    "مانده:368,067\n" +
                    "1219-12:47"
            ,
            "بانک ملي\n" +
                    "خودپرداز:200,000-\n" +
                    "حساب:92006\n" +
                    "مانده:135,323\n" +
                    "0905-07:47"
            ,
            "بانک ملي\n" +
                    "برداشت:300,000-\n" +
                    "حساب:92006\n" +
                    "مانده:936,529\n" +
                    "0829-16:53"
            ,
            "بانک ملي\n" +
                    "انتقال:605,000-\n" +
                    "حساب:92006\n" +
                    "مانده:3,523,529\n" +
                    "0817-02:17"
            ,
            "بانک آينده\n" +
                    "واريز خودپرداز\n" +
                    "انتقال به كارت\n" +
                    "از کارت:5846\n" +
                    "به کارت:3895\n" +
                    "مبلغ:3,000,000\n" +
                    "حساب:01002\n" +
                    "بانک عامل:بانك تجارت\n" +
                    "مانده:3,079,748\n" +
                    "1396/10/03\n" +
                    "ساعت:10:51:58"
            ,
            "بانک آينده\n" +
                    "برداشت POS/PINPAD\n" +
                    "خريد با POS\n" +
                    "مبلغ:800,000\n" +
                    "حساب:01002\n" +
                    "مانده:1,007,498\n" +
                    "1396/10/03\n" +
                    "ساعت:19:07:49"
    )
}
