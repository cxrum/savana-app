package com.savana.data.local.user

interface UserDao {

    fun getId(): Int?
    fun setId(id: Int)

    fun getUsername(): String?
    fun setUsername(name: String)


}