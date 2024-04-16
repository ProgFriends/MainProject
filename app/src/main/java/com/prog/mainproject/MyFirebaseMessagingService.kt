package com.prog.mainproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM","From :"+remoteMessage!!.from)
        super.onMessageReceived(remoteMessage)

        createNotificationChannel() // 알림 채널 생성

        // 푸시 알림 생성
        createNotification(remoteMessage)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onMessageSent(msgId: String) {
        super.onMessageSent(msgId)
    }

    override fun onSendError(msgId: String, exception: Exception) {
        super.onSendError(msgId, exception)
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "new Token: $token")
        super.onNewToken(token)
    }

    private fun createNotification(remoteMessage: RemoteMessage) {
        // 푸시 알림 내용 설정
        val notificationTitle = remoteMessage.notification?.title ?: "알림"
        val notificationBody = remoteMessage.notification?.body ?: "내용이 없습니다."
        val channelId = "my_channel_id"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.sprout)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setAutoCancel(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0 // 고유한 ID로 설정
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "My Channel Description"

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}