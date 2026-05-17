package com.example.triplink.core.utils

sealed class RequestResult {
    data class Success(val message: String) : RequestResult()
    data class Failure(val errorMessage: String) : RequestResult()
    object Loading : RequestResult()
}

@Suppress("unused")
fun RequestResult.messageText(): String = when (this) {
    is RequestResult.Success -> message
    is RequestResult.Failure -> errorMessage
    is RequestResult.Loading -> ""
}

@Suppress("unused")
val RequestResult.isErrorResult: Boolean
    get() = this is RequestResult.Failure

@Suppress("unused")
val RequestResult.isLoading: Boolean
    get() = this is RequestResult.Loading

