package com.savana.data.local.user

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserDaoImpl(
    context: Context
): UserDao {

    companion object {
        private const val PREFS_NAME = "user_data_prefs"
        private const val KEY_USER_TOKEN = "user_token"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getId(): Int? {
        val data = sharedPreferences.getInt(KEY_USER_TOKEN, -1)
        return  if (data == -1) null else data
    }

    override fun setId(id: Int) {
        sharedPreferences.edit{ putInt(KEY_USER_TOKEN, id) }
    }

}