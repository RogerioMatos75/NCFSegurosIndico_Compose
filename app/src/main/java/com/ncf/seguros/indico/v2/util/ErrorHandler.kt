package com.ncf.seguros.indico.v2.util

import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Representa o resultado de uma operação que pode falhar
 * @param T tipo do resultado em caso de sucesso
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

/**
 * Extensão para converter um Flow em um Flow<Result> Captura exceções e as converte em Result.Error
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> =
        map<T, Result<T>> { Result.Success(it) }.catch { e ->
            // Não capturamos CancellationException para permitir o cancelamento adequado de
            // corrotinas
            if (e is CancellationException) throw e
            emit(Result.Error(e as Exception))
        }

/**
 * Executa uma operação suspensa com tratamento de erro padronizado
 * @param operation a operação a ser executada
 * @return Result contendo sucesso ou erro
 */
suspend fun <T> safeCall(operation: suspend () -> T): Result<T> =
        try {
            Result.Success(operation())
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.Error(e)
        }
