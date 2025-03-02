package com.ncf.seguros.indico.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ncf.seguros.indico.MainActivity
import com.ncf.seguros.indico.R
import com.ncf.seguros.indico.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Verificar se a mensagem contém dados
        remoteMessage.data.isNotEmpty().let {
            // Processar os dados da mensagem
            handleDataMessage(remoteMessage.data)
        }

        // Verificar se a mensagem contém uma notificação
        remoteMessage.notification?.let {
            // Mostrar a notificação
            sendNotification(it.title ?: "NCF Seguros", it.body ?: "Nova notificação")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // Salvar o novo token no Firestore
        CoroutineScope(Dispatchers.IO).launch {
            userRepository.updateFCMToken(token)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "NCF Seguros"
        val message = data["message"] ?: "Nova notificação"
        val type = data["type"]
        val id = data["id"]
        
        // Dependendo do tipo, podemos realizar ações diferentes
        when (type) {
            "new_indication" -> {
                // Notificação de nova indicação (para administradores)
                sendNotification(title, message, "admin_indications", id)
            }
            "indication_status_update" -> {
                // Notificação de atualização de status de indicação (para usuários)
                sendNotification(title, message, "my_indications", id)
            }
            else -> {
                // Notificação genérica
                sendNotification(title, message)
            }
        }
    }

    private fun sendNotification(title: String, messageBody: String, action: String? = null, id: String? = null) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (action != null && id != null) {
                putExtra("action", action)
                putExtra("id", id)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
} 