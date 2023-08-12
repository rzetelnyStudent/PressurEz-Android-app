package com.MichalKapuscinski.BikeTPMS.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.MichalKapuscinski.BikeTPMS.MainActivity
import com.MichalKapuscinski.BikeTPMS.models.Bike
import com.MichalKapuscinski.BikeTPMS.ui.formatNullablePressure


class MyNotificationManager(context: Context, channelName: String, channelDescription: String) {
    private val channelId = channelName
    private val context = context

    init {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = channelDescription
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(bike: Bike) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(bike.appearance)
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


        //notificationCreated = true
        //with(NotificationManagerCompat.from(context)) {
        //    notify(notificationId, builder.build())
        //}
    }

    private fun isNotificationVisible(bike: Bike): Boolean {
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

    fun postNotificationConditionally(bike: Bike, isForeground: Boolean) {
        when (bike.notificationState) {
            NotificationState.NOT_VISIBLE -> {
                if (!isForeground) {
                    sendNotification(bike)
                    bike.notificationState = NotificationState.VISIBLE
                }
            }
            NotificationState.VISIBLE -> {
                if (isNotificationVisible(bike)) {
                    if (!isForeground) {
                        sendNotification(bike)     // actually update a notification                       
                    }
                    else {
                        deleteNotification(bike)
                    }
                } else {     // it means the notification was dismissed
                    bike.notificationState = NotificationState.DISMISSED
                }
            }
            NotificationState.DISMISSED -> {}     // could check here if some sort of timeout passed since the notification was dismissed
        }
    }

    private fun deleteNotification(bike: Bike) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(bike.id)
    }

}