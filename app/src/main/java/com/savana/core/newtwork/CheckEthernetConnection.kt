package com.savana.core.newtwork

import kotlinx.coroutines.flow.first

suspend fun isEthernetConnected(connectivityObserver: ConnectivityObserver): Boolean{
    val hasConnection = connectivityObserver.observe()
//    return hasConnection.first()
    return true
}
