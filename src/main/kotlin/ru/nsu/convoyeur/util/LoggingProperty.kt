package ru.nsu.convoyeur.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Delegate, which provides logger for each class
 */
class LoggerProperty : ReadOnlyProperty<Any?, Logger> {

    companion object {
        private fun <T> createLogger(clazz: Class<T>) = LoggerFactory.getLogger(clazz)
    }

    private var logger: Logger? = null

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        if (logger == null) {
            logger = createLogger(thisRef!!.javaClass)
        }
        return logger!!
    }
}

fun Logger.ifDebug(message: String, vararg arguments: Any) {
    if (this.isDebugEnabled) {
        this.debug(message, arguments)
    }
}