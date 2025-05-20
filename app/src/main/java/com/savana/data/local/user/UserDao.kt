package com.savana.data.local.user

interface UserDao {

    fun getToken(): String?
    fun setToken(token: String)


}