package com.ncf.seguros.indico.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ncf.seguros.indico.MainActivity
import com.ncf.seguros.indico.R
import com.ncf.seguros.indico.repository.CondominiumRepository
import com.ncf.seguros.indico.repository.InsuranceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class InsuranceCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val condominiumRepository: CondominiumRepository,
    private val insuranceRepository: InsuranceRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            // Obter todos os condomínios do usuário
            val condominiums = condominiumRepository.getCondominiumsForUser().first()
            
            // Para cada condomínio, verificar os seguros
            for (condominium in condominiums) {
                val insurances = insuranceRepository.getInsurancesForCondominium(condominium.id).first()
                
                // Verificar seguros prestes a vencer (menos de 30 dias)
                val expiringInsurances = insurances.filter { 
                    it.isActive() && it.getRemainingDays() < 30 
                }
                
                // Enviar notificações para cada seguro prestes a vencer
                for (insurance in expiringInsurances) {
                    val remainingDays = insurance.getRemainingDays()
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val endDateText = dateFormat.format(Date(insurance.endDate))
                    
                    val title = "Seguro prestes a vencer"
                    val message = "O seguro ${insurance.policyNumber} do condomínio ${condominium.name} vence em $remainingDays dias ($endDateText)."
                    
                    sendNotification(
                        applicationContext,
                        title,
                        message,
                        "insurance_detail",
                        insurance.id,
                        insurance.hashCode()
                    )
                }
            }
            
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun sendNotification(
        context: Context,
        title: String,
        messageBody: String,
        action: String? = null,
        id: String? = null,
        notificationId: Int
    ) {
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
            .setContentText(messageBody)
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

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
} 