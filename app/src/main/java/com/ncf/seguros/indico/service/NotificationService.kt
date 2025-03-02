package com.ncf.seguros.indico.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.ncf.seguros.indico.MainActivity
import com.ncf.seguros.indico.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val firestore = FirebaseFirestore.getInstance()
    
    // Enviar notificação local
    fun sendLocalNotification(title: String, message: String, action: String? = null, id: String? = null) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (action != null && id != null) {
                putExtra("action", action)
                putExtra("id", id)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = context.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Criar o canal de notificação para Android O e superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Canal de Notificações NCF Seguros",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
    
    // Enviar notificação push para um usuário específico
    suspend fun sendPushNotification(userId: String, title: String, message: String, data: Map<String, String> = emptyMap()) {
        try {
            // Obter o token FCM do usuário
            val userDoc = firestore.collection("users").document(userId).get().await()
            val fcmToken = userDoc.getString("fcmToken")
            
            if (fcmToken != null) {
                // Construir a mensagem
                val messageBuilder = RemoteMessage.Builder("$fcmToken@fcm.googleapis.com")
                    .setMessageId(System.currentTimeMillis().toString())
                
                // Adicionar dados
                val allData = HashMap<String, String>().apply {
                    put("title", title)
                    put("message", message)
                    putAll(data)
                }
                
                messageBuilder.setData(allData)
                
                // Enviar a mensagem
                FirebaseMessaging.getInstance().send(messageBuilder.build())
            }
        } catch (e: Exception) {
            // Lidar com erros
            e.printStackTrace()
        }
    }
    
    // Notificar o usuário que fez a indicação quando o link é enviado
    suspend fun notifyReferrerAboutLinkSent(referrerId: String, indicationName: String) {
        val title = "Parabéns pela sua indicação!"
        val message = "Sua indicação para $indicationName foi contatada. Você acumulou mais 1% de desconto na renovação do seu seguro!"
        
        // Enviar notificação push
        sendPushNotification(
            userId = referrerId,
            title = title,
            message = message,
            data = mapOf(
                "type" to "indication_contacted",
                "action" to "my_indications"
            )
        )
        
        // Também enviar notificação local caso o usuário esteja usando o app
        sendLocalNotification(title, message, "my_indications")
    }
} 