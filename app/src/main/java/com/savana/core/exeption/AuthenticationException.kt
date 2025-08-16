package com.savana.core.exeption

open class AuthenticationException(msg: String?): Exception(msg)

class RegistrationFailedException(msg: String? = null): AuthenticationException(msg)

class LoginFailedException(msg: String? = null): AuthenticationException(msg)