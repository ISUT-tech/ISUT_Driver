package com.isut.cabdriver

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import android.app.PendingIntent
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import android.media.RingtoneManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //Displaying data in log
        //It is optional// fair
        Log.d(TAG, "Notification Message Body: " + remoteMessage.data["body"])
        if (!remoteMessage.data.isEmpty()) {
            Log.d(TAG, "Notification Message Body: " + remoteMessage.data["body"])
            Log.d(TAG, "Notification Message Body: " + remoteMessage.data["extra"])
            // Log.d(TAG, "Notification: " + remoteMessage.getData().get("user_id"));
            val data = remoteMessage.data
            val userid = data["userId"]
            val driverid = data["driverId"]
            val name = data["name"]
            val source = data["sourceLocation"]
            val destination = data["destinationLocation"]
            val fair = data["fair"]
            val discount = data["discount"]
            val bookingid = data["bookingId"]!!.toInt()
            val isInvoice = data["isInvoice"]!!.toBoolean()


            //  Log.d(TAG, "Notification2: " +  remoteMessage.getData());

            // Log.d(TAG, "Notification Message Body: titlt " + remoteMessage.getData().get("title"));

            //Calling method to generate notification
            sendNotification(
                remoteMessage.data["body"],
                name,
                userid,
                driverid,
                source,
                destination,
                fair,
                bookingid,isInvoice,discount!!
            )
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private fun sendNotification(
        body: String?,
        names: String?,
        userid: String?,
        driverid: String?,
        source: String?,
        destination: String?,
        fair: String?,
        bookingid: Int,
        isInvoice: Boolean,
        discount: String
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("key", "fromonti")
        intent.putExtra("name", names)
        intent.putExtra("userid", userid)
        intent.putExtra("driverid", driverid)
        intent.putExtra("source", source)
        intent.putExtra("destination", destination)
        intent.putExtra("fair", fair)
        intent.putExtra("bookingoid", bookingid)
        intent.putExtra("isInvoice", isInvoice)
        intent.putExtra("discount", discount)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "YOUR CHANNEL"
            val description = "YOUR CHANNEL DESCRIPTION"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("YOUR CHANNEL ID", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = this@MyFirebaseMessagingService.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.cansing)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.cansing))
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Cab Booking Request")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder.setChannelId("YOUR CHANNEL ID")
        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}