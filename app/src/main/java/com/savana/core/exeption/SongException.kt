package com.savana.core.exeption

open class SongException(msg: String?): Exception(msg)

class SongToLongException(msg: String?): SongException(msg)

class SongToShortException(msg: String?): SongException(msg)

class SongInvalidFormatException(msg: String?): SongException(msg)
