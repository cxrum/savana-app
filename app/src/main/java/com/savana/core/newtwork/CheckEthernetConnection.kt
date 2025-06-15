package com.savana.core.newtwork

import kotlinx.coroutines.flow.first

suspend fun isEthernetConnected(connectivityObserver: ConnectivityObserver): Boolean{
    val hasConnection = connectivityObserver.observe()
    if (hasConnection.first()) {
        return false
    }
    return true
}
