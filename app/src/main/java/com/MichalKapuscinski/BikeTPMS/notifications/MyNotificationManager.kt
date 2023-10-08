package com.MichalKapuscinski.BikeTPMS.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.MichalKapuscinski.BikeTPMS.MainActivity
import com.MichalKapuscinski.BikeTPMS.R
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.ui.formatNullablePressure
import com.MichalKapuscinski.BikeTPMS.ui.resourceUri


class MyNotificationManager(context: Context, channelName: String, channelDescription: String) {
    private val channelId = channelName + "e"   // temporary fix, resetting channel would be the best
    //private val context = context

    init {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = channelDescription
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            notificationChannel.setSound(context.resourceUri(R.raw.ok_sound), audioAttributes)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(bike: Bike, context: Context) {

        val builder = NotificationCompat.Builder(context, channelId)    // .setCategory(Notification.CATEGORY_STATUS)
            .setSubText(if (bike.isPressureLow()) {context.getString(R.string.warning_icon)} else {null})
            .setSmallIcon(R.drawable.ic_bike)
            .setSilent(!bike.isPressureLow())
            .setContentTitle(bike.name)
            .setContentText("Rear: ${formatNullablePressure(bike.sensorRear.pressureBar)}bar, Front: ${formatNullablePressure(bike.sensorFront.pressureBar)}bar")
            .setOnlyAlertOnce(true)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntent(Intent(context, MainActivity::class.java))
        // tu trzeba bedzie troce zmienic zeby nawigacja dzialala: https://proandroiddev.com/all-about-notifications-in-android-718961054961#:~:text=2%3A%20To%20start-,an,-activity%20that%20includes
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(resultPendingIntent)

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(bike.id, builder.build())
    }

    private fun isNotificationVisible(bike: Bike, context: Context): Boolean {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notifications = notificationManager.activeNotifications
        for (notification in notifications) {
            if (notification.id == bike.id) {
                return true
            }
        }
        return false
    }

    public fun resetState(bike: Bike) {
        if (bike.notificationState == NotificationState.DISMISSED) {
            bike.notificationState = NotificationState.NOT_VISIBLE
        }
    }

    fun postNotificationConditionally(bike: Bike, isForeground: Boolean, context: Context) {
        if (bike.hasPressureChanged()) {
            when (bike.notificationState) {
                NotificationState.NOT_VISIBLE -> {
                    if (!isForeground) {
                        sendNotification(bike, context)
                        bike.notificationState = NotificationState.VISIBLE
                    }
                }

                NotificationState.VISIBLE -> {
                    if (isNotificationVisible(bike, context)) {
                        if (!isForeground) {
                            sendNotification(bike, context)     // actually update a notification
                        } else {
                            deleteNotification(bike, context)
                            //Log.d("noti", "deleting notification")
                        }
                    } else {     // it means the notification was dismissed
                        bike.notificationState = NotificationState.DISMISSED
                    }
                }

                NotificationState.DISMISSED -> {}     // could check here if some sort of timeout passed since the notification was dismissed
            }
        }
    }

    private fun deleteNotification(bike: Bike, context: Context) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(bike.id)
    }

}