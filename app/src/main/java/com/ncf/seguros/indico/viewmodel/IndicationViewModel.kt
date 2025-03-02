package com.ncf.seguros.indico.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ncf.seguros.indico.model.Indication
import com.ncf.seguros.indico.repository.IndicationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IndicationViewModel @Inject constructor(
    private val indicationRepository: IndicationRepository
) : ViewModel() {
    
    private val _indicationsState = MutableStateFlow<IndicationsState>(IndicationsState.Loading)
    val indicationsState: StateFlow<IndicationsState> = _indicationsState
    
    private val _indicationState = MutableStateFlow<IndicationState>(IndicationState.Initial)
    val indicationState: StateFlow<IndicationState> = _indicationState
    
    private val _userDiscountState = MutableStateFlow<UserDiscountState>(UserDiscountState.Loading)
    val userDiscountState: StateFlow<UserDiscountState> = _userDiscountState
    
    fun loadUserIndications() {
        viewModelScope.launch {
            _indicationsState.value = IndicationsState.Loading
            try {
                indicationRepository.getIndicationsForCurrentUser().collect { indications ->
                    _indicationsState.value = IndicationsState.Success(indications)
                }
            } catch (e: Exception) {
                _indicationsState.value = IndicationsState.Error(e.message ?: "Erro ao carregar indicações")
            }
        }
    }
    
    fun loadAllIndications() {
        viewModelScope.launch {
            _indicationsState.value = IndicationsState.Loading
            try {
                indicationRepository.getAllIndications().collect { indications ->
                    _indicationsState.value = IndicationsState.Success(indications)
                }
            } catch (e: Exception) {
                _indicationsState.value = IndicationsState.Error(e.message ?: "Erro ao carregar indicações")
            }
        }
    }
    
    fun addIndication(indication: Indication) {
        viewModelScope.launch {
            _indicationState.value = IndicationState.Loading
            val result = indicationRepository.addIndication(indication)
            result.fold(
                onSuccess = { id ->
                    _indicationState.value = IndicationState.Added(id)
                },
                onFailure = { e ->
                    _indicationState.value = IndicationState.Error(e.message ?: "Erro ao adicionar indicação")
                }
            )
        }
    }
    
    fun updateIndicationStatus(indicationId: String, status: String, notes: String = "") {
        viewModelScope.launch {
            _indicationState.value = IndicationState.Loading
            val result = indicationRepository.updateIndicationStatus(indicationId, status, notes)
            result.fold(
                onSuccess = {
                    _indicationState.value = IndicationState.Updated
                },
                onFailure = { e ->
                    _indicationState.value = IndicationState.Error(e.message ?: "Erro ao atualizar status da indicação")
                }
            )
        }
    }
    
    fun applyDiscount(indicationId: String) {
        viewModelScope.launch {
            _indicationState.value = IndicationState.Loading
            val result = indicationRepository.applyDiscount(indicationId)
            result.fold(
                onSuccess = {
                    _indicationState.value = IndicationState.Updated
                },
                onFailure = { e ->
                    _indicationState.value = IndicationState.Error(e.message ?: "Erro ao aplicar desconto")
                }
            )
        }
    }
    
    fun deleteIndication(indicationId: String) {
        viewModelScope.launch {
            _indicationState.value = IndicationState.Loading
            val result = indicationRepository.deleteIndication(indicationId)
            result.fold(
                onSuccess = {
                    _indicationState.value = IndicationState.Deleted
                },
                onFailure = { e ->
                    _indicationState.value = IndicationState.Error(e.message ?: "Erro ao excluir indicação")
                }
            )
        }
    }
    
    fun loadUserDiscount() {
        viewModelScope.launch {
            _userDiscountState.value = UserDiscountState.Loading
            val result = indicationRepository.getUserTotalDiscount()
            result.fold(
                onSuccess = { discount ->
                    _userDiscountState.value = UserDiscountState.Success(discount)
                },
                onFailure = { e ->
                    _userDiscountState.value = UserDiscountState.Error(e.message ?: "Erro ao calcular desconto")
                }
            )
        }
    }
    
    fun sendInterestLink(indication: Indication) {
        viewModelScope.launch {
            _indicationState.value = IndicationState.Loading
            
            try {
                // URL do formulário de interesse fornecido
                val interestFormUrl = "https://villa.segfy.com/Publico/Segurados/Orcamentos/SolicitarCotacao?e=P6pb0nbwjHfnbNxXuNGlxw%3D%3D"
                
                // Mensagem personalizada
                val message = "Olá ${indication.name}, parabéns! Você foi indicado(a) por um amigo para conhecer as vantagens exclusivas da NCF Seguros. " +
                              "Clique no link para solicitar sua cotação: $interestFormUrl"
                
                // Criar intent para WhatsApp
                val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                    // Formatar o número de telefone (remover caracteres não numéricos)
                    val formattedPhone = indication.phone.replace(Regex("[^0-9]"), "")
                    // Adicionar código do país se não estiver presente
                    val phoneWithCountryCode = if (!formattedPhone.startsWith("55")) "55$formattedPhone" else formattedPhone
                    
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneWithCountryCode&text=${Uri.encode(message)}")
                }
                
                // Criar intent para email como alternativa
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(indication.email))
                    putExtra(Intent.EXTRA_SUBJECT, "NCF Seguros - Proposta Especial")
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                
                // Criar intent chooser para permitir que o usuário escolha entre WhatsApp, email ou outras opções
                val chooserIntent = Intent.createChooser(emailIntent, "Enviar link via")
                
                // Adicionar WhatsApp como opção no chooser
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(whatsappIntent))
                
                // Emitir evento para a UI
                _sendLinkEvent.emit(SendLinkEvent(chooserIntent, indication.id))
                
                _indicationState.value = IndicationState.LinkSent
            } catch (e: Exception) {
                _indicationState.value = IndicationState.Error(e.message ?: "Erro ao enviar link")
            }
        }
    }
    
    // Evento para enviar o link
    private val _sendLinkEvent = MutableStateFlow<SendLinkEvent>()
    val sendLinkEvent = _sendLinkEvent.asStateFlow()
    
    data class SendLinkEvent(val intent: Intent, val indicationId: String)
    
    sealed class IndicationsState {
        object Loading : IndicationsState()
        data class Success(val indications: List<Indication>) : IndicationsState()
        data class Error(val message: String) : IndicationsState()
    }
    
    sealed class IndicationState {
        object Initial : IndicationState()
        object Loading : IndicationState()
        data class Added(val id: String) : IndicationState()
        object Updated : IndicationState()
        object Deleted : IndicationState()
        data class Error(val message: String) : IndicationState()
        object LinkSent : IndicationState()
    }
    
    sealed class UserDiscountState {
        object Loading : UserDiscountState()
        data class Success(val discount: Int) : UserDiscountState()
        data class Error(val message: String) : UserDiscountState()
    }
} 