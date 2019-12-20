package com.skywavestudios.receipthistory

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.support.v4.app.NotificationCompat.getExtras
import android.os.Bundle
import android.content.Intent
import android.provider.Settings.Global.getString
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import android.telephony.SmsMessage
import android.app.PendingIntent
import android.support.v4.app.NotificationManagerCompat
import android.widget.Toast
import android.R.attr.data
import android.app.Notification
import android.media.RingtoneManager
import android.R.attr.name
import android.app.NotificationChannel
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Telephony
import android.util.Log
import com.skywavestudios.receipthistory.shared.Analyzer
import com.skywavestudios.receipthistory.shared.model.Receipt
import com.skywavestudios.receipthistory.shared.model.ReceiptDBHelper
import java.util.*


/**
 * Created by zarin on 11/17/2017.
 */
class Reciever_SMS : BroadcastReceiver() {
    val KEY_REPLY = "REPLY_TO_SMS"
    val REPLY_ACTION = null
    val INTENT_EXTRA = "intentID"
    var NOTIFICATION_KEY = "notifyKey"
    val GroupKey = 8585

    override fun onReceive(context: Context, intent: Intent) {

        var messageReceipt: Receipt? = null
        var code = 1000;
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras           //---get the SMS message passed in---
            var msgs: Array<SmsMessage>?
            var msg_from: String
            var msg_id: String
            val random = Random();
            code = random.nextInt(9999 - 1000)
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    var messageBody = ""
                    val pdus = bundle.get("pdus") as Array<Any>
                    val messages = arrayOfNulls<SmsMessage>(pdus.size)
                    for (i in pdus.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    }
                    if (messages.size > -1) {
                        for (i in pdus.indices) {
                            messageBody += messages[i]?.displayMessageBody
                        }
                    }
                    var a = Analyzer(context)
                    var r = a.AnalyzeSms(messageBody)
                    if (r != null) {
                        msg_id = ConstantsApp.addtoDB(r, context)

                        //notification
                        var intent2 = Intent(context, Reciever_SMS::class.java)
                        intent2.action = REPLY_ACTION
                        intent2.putExtra(INTENT_EXTRA, msg_id)
                        intent2.putExtra(NOTIFICATION_KEY, code)
                        //intent2.putExtra("body", 55)
                        val pending_intent = PendingIntent.getBroadcast(context, code, intent2,
                                PendingIntent.FLAG_ONE_SHOT)


                        val replyLabel = context.getString(R.string.notification_text)
                        val remoteInput = RemoteInput.Builder(KEY_REPLY)
                                .setLabel(replyLabel)
                                .build()

                        val replyAction = NotificationCompat.Action.Builder(
                                R.drawable.fi_ghavamin, replyLabel, pending_intent)
                                .addRemoteInput(remoteInput)
                                .setAllowGeneratedReplies(true)
                                .build()

                        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        val mBuilder = NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.fi_unknown)
                                .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.plus))
                                .setContentTitle(context.getString(R.string.notification_title))
                                .setContentText(" مبلغ: " + ConstantsApp.Int_To_Price(r.Price.toLong()))
                                .setContentInfo(ConstantsApp.getBankName(r.FinancialInstituteId, context))
                                .setShowWhen(true)
                                .addAction(replyAction) // reply action from step b above
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setGroup(GroupKey.toString())
                                .setStyle(NotificationCompat.BigTextStyle().bigText(""))

                        //val mNotificationManager = NotificationManagerCompat.from(context)
                        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channelId = "my_channel_01"
                            val mChannel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel(channelId, "MyChannel", NotificationManager.IMPORTANCE_HIGH)
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }
                            mNotificationManager.createNotificationChannel(mChannel)
                        } else {

                        }

                        mNotificationManager.notify(code, mBuilder.build())

                    }
                } catch (e: Exception) {
                    Log.d("Exception caught", e.toString());
                }

            }
        }

        if (REPLY_ACTION.equals(intent.action)) {
            val message = getReplyMessage(intent)
            val messageId = intent.getStringExtra(INTENT_EXTRA)
            val notifId = intent.getIntExtra(NOTIFICATION_KEY, 0)

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancel(notifId)
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;


            //Toast.makeText(context, "Message ID: $messageId\nMessage: $message",
            //        Toast.LENGTH_SHORT).show()
            //reply has been used notification
            var _dbHelper = ReceiptDBHelper(context, ConstantsApp.DATABASE_VERSION)
            var mDB = _dbHelper.getWritableDatabase()
            messageReceipt = ConstantsApp.getReceipt(messageId, mDB)
            if (messageReceipt != null) {
                messageReceipt.Title = message.toString()
                ConstantsApp.savetoDB(messageReceipt, context)
            }

            /*val notificationManager = NotificationManagerCompat.from(context)

            val builder = NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentText(context.getString(R.string.notification_to_reply_text))
                    .setShowWhen(true)

            notificationManager.notify(555, builder.build())*/
        }
    }


    private fun getReplyMessage(intent: Intent): CharSequence? {
        val remoteInput = RemoteInput.getResultsFromIntent(intent)
        return remoteInput?.getCharSequence(KEY_REPLY)
    }
}