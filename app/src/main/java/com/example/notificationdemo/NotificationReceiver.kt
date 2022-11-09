package com.example.notificationdemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.*
import com.example.notificationdemo.util.DataStoreManager
import com.example.notificationdemo.util.PreferenceKeys.NOTIFICATION_TONE_URI_KEY

class NotificationReceiver : BroadcastReceiver(), LifecycleOwner {

    private var notificationManager: NotificationManager? = null
    lateinit var notificationChannel: NotificationChannel
    val channelID = "WaterIT"
    lateinit var notificationBuilder: NotificationCompat.Builder

    lateinit var lifecycleRegistry: LifecycleRegistry

    lateinit var dataStoreManager: DataStoreManager

        override fun onReceive(context: Context?, p1: Intent?) {

            lifecycleRegistry = LifecycleRegistry(this)
            lifecycleRegistry.markState(Lifecycle.State.CREATED)

        dataStoreManager = DataStoreManager(context!!)
        pushNotification(context!!)

    }

    private fun pushNotification(context: Context) {

        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // tapResultIntent gets executed when user taps the notification
        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapResultIntent,
            FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//            val notificationsNewMessageRingtone = prefs.getString(
//                AppUtils.NOTIFICATION_TONE_URI_KEY, RingtoneManager.getDefaultUri(
//                    RingtoneManager.TYPE_NOTIFICATION
//                ).toString()
//            )
            var notificationRingTone = ""
                dataStoreManager.readStringFromDataStore(NOTIFICATION_TONE_URI_KEY).asLiveData().observe(this, Observer {
                    notificationRingTone = it
                })

            Log.d("BROAD_CAST_LOG_TONE", "pushNotification: "+ notificationRingTone)

            notificationChannel = NotificationChannel(
                channelID,
                "Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            notificationManager?.createNotificationChannel(notificationChannel)
            notificationBuilder =
                NotificationCompat.Builder(context, channelID)
                    .setAutoCancel(true)
                    .setContentTitle("Drink Water...")
                    .setContentText("It's time to hydrate body")
                    .setSmallIcon(R.mipmap.h2o)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(longArrayOf(0, 100, 100, 100, 100))
//                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)

        } else {
            notificationBuilder =
                NotificationCompat.Builder(context, channelID)
                    .setAutoCancel(true)
                    .setContentTitle("Drink Water...")
                    .setContentText("It's time to hydrate body")
                    .setSmallIcon(R.mipmap.h2o)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
        }

       /* notificationManager = context?.let { ctx ->
            NotificationManagerCompat.from(ctx)
        }*/

        notificationManager!!.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun getLifecycle(): Lifecycle {
       return lifecycleRegistry
    }

}