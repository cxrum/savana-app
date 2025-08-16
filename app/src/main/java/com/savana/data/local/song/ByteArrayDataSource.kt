package com.savana.data.local.song

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import java.io.IOException
import kotlin.math.min

@UnstableApi
class ByteArrayDataSource(private val data: ByteArray) : BaseDataSource(true) {
    private var uri: Uri? = null
    private var bytesRemaining: Int = 0
    private var readPosition: Int = 0

    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri
        transferInitializing(dataSpec)

        readPosition = dataSpec.position.toInt()
        bytesRemaining = if (dataSpec.length != C.LENGTH_UNSET.toLong()) {
            dataSpec.length.toInt()
        } else {
            data.size - readPosition
        }

        if (bytesRemaining < 0 || readPosition + bytesRemaining > data.size) {
            throw IOException("Invalid range: $readPosition, $bytesRemaining, size=${data.size}")
        }

        transferStarted(dataSpec)
        return bytesRemaining.toLong()
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) {
            return 0
        }
        if (bytesRemaining == 0) {
            return C.RESULT_END_OF_INPUT
        }

        val bytesToRead = min(length, bytesRemaining)
        System.arraycopy(data, readPosition, buffer, offset, bytesToRead)
        readPosition += bytesToRead
        bytesRemaining -= bytesToRead
        bytesTransferred(bytesToRead)
        return bytesToRead
    }

    override fun getUri(): Uri? {
        return uri
    }

    override fun close() {
        if (uri != null) {
            uri = null
            transferEnded()
        }
    }

    class Factory(private val data: ByteArray) : DataSource.Factory {
        override fun createDataSource(): DataSource {
            return ByteArrayDataSource(data)
        }
    }
}