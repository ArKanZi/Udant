package com.arkanzi.udant.core.logging

import android.util.Log
import com.arkanzi.udant.BuildConfig
import com.arkanzi.udant.core.logging.model.LogLevel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class AppLogger @Inject constructor() {

    private fun log(
        level: LogLevel,
        tag: String,
        message: String,
        throwable: Throwable? = null
    ) {
        if (!BuildConfig.DEBUG && level == LogLevel.DEBUG) {
            return
        }
        when (level) {
            LogLevel.DEBUG -> Log.d(tag, message)
            LogLevel.INFO -> Log.i(tag, message)
            LogLevel.WARNING -> Log.w(tag, message)
            LogLevel.ERROR -> Log.e(tag, message, throwable)
        }
    }

    fun debug(tag: String, message: String) =
        log(LogLevel.DEBUG, tagName(tag), message)

    fun debug(tag: KClass<*>, message: String) =
        debug(tagName(tag), message)

    fun info(tag: String, message: String) =
        log(LogLevel.INFO, tagName(tag), message)


    fun info(tag: KClass<*>, message: String) =
        info(tagName(tag), message)

    fun warning(tag: String, message: String) =
        log(LogLevel.WARNING, tagName(tag), message)


    fun warning(tag: KClass<*>, message: String) =
        warning(tagName(tag), message)

    fun error(tag: String, message: String, throwable: Throwable?) =
        log(LogLevel.ERROR, tagName(tag), message, throwable)


    fun error(tag: KClass<*>, message: String, throwable: Throwable?) =
        error(tagName(tag), message, throwable)

    private fun tagName(tag: String) = TAG_PREFIX + tag

    private fun tagName(tag: KClass<*>) = tag.java.simpleName

    companion object {
        const val TAG_PREFIX = "DevDefine-"
    }
}