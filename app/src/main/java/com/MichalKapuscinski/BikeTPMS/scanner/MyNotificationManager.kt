package com.MichalKapuscinski.BikeTPMS.scanner

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
import androidx.core.content.contentValuesOf
import com.MichalKapuscinski.BikeTPMS.MainActivity
import com.MichalKapuscinski.BikeTPMS.R
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

    public fun sendNotification(bike: Bike) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(bike.appearance)
            .setContentTitle(bike.name)
            .setContentText("Rear: ${formatNullablePressure(bike.sensorRear.pressureBar)}bar, Front: ${formatNullablePressure(bike.sensorFront.pressureBar)}bar")
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

}