package com.savana.core.exeption

open class UserException(msg: String?): Exception(msg)

class NoTempedUserException(msg: String? = null): UserException(msg)