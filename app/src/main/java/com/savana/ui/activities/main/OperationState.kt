package com.savana.ui.activities.main

sealed class OperationState<out T> {
    object Idle : OperationState<Nothing>()
    data class  Loading(val msg: String? = null, val canLeaveScreen: Boolean = true) : OperationState<Nothing>()
    data class Success<T>(val data: T) : OperationState<T>()
    data class Error(val errorMessage: String) : OperationState<Nothing>()
}