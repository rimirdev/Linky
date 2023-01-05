package com.rimir.linky.util

import java.lang.Exception

object URLSUtils {
    fun getExtension(url: String): String {
        return try {
            url.substring(url.lastIndexOf("."))
        } catch (e: Exception) {
            e.printStackTrace()
            "error"
        }
    }
}