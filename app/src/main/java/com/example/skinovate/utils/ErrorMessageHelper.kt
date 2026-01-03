package com.example.skinovate.utils

import android.database.sqlite.SQLiteException
import java.io.IOException
import java.net.UnknownHostException

/**
 * Helper untuk convert exception ke user-friendly error messages
 */
object ErrorMessageHelper {
    
    /**
     * Convert exception ke user-friendly message dalam Bahasa Indonesia
     */
    fun getErrorMessage(exception: Throwable?): String {
        if (exception == null) {
            return "Terjadi kesalahan yang tidak diketahui"
        }
        
        return when (exception) {
            is UnknownHostException, is IOException -> {
                "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
            }
            is SQLiteException -> {
                "Terjadi kesalahan pada database. Silakan coba lagi."
            }
            is IllegalStateException -> {
                "Aplikasi dalam keadaan tidak valid. Silakan restart aplikasi."
            }
            is IllegalArgumentException -> {
                "Data yang dimasukkan tidak valid. Silakan periksa kembali."
            }
            is NullPointerException -> {
                "Data tidak ditemukan. Silakan coba lagi."
            }
            is SecurityException -> {
                "Izin tidak diberikan. Silakan berikan izin yang diperlukan."
            }
            else -> {
                // Generic error message
                "Terjadi kesalahan: ${exception.message ?: "Silakan coba lagi"}"
            }
        }
    }
    
    /**
     * Get error message untuk database operations
     */
    fun getDatabaseErrorMessage(exception: Throwable?): String {
        return when (exception) {
            is SQLiteException -> {
                "Gagal menyimpan data. Silakan coba lagi."
            }
            is IllegalStateException -> {
                "Database belum siap. Silakan tunggu sebentar dan coba lagi."
            }
            else -> {
                getErrorMessage(exception)
            }
        }
    }
    
    /**
     * Get error message untuk network operations
     */
    fun getNetworkErrorMessage(exception: Throwable?): String {
        return when (exception) {
            is UnknownHostException, is IOException -> {
                "Tidak dapat terhubung. Periksa koneksi internet Anda."
            }
            else -> {
                getErrorMessage(exception)
            }
        }
    }
}

