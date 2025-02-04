package dev.gitlive.firebase.perf

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseException
import dev.gitlive.firebase.firebase
import dev.gitlive.firebase.perf.metrics.Trace

actual val Firebase.performance: FirebasePerformance
    get() = rethrow {
        dev.gitlive.firebase.performance
        FirebasePerformance(firebase.performance())
    }

actual fun Firebase.performance(app: FirebaseApp): FirebasePerformance = rethrow {
    dev.gitlive.firebase.performance
    FirebasePerformance(firebase.performance(app.js))
}

actual class FirebasePerformance internal constructor(val js: firebase.performance) {

    actual fun newTrace(traceName: String): Trace = rethrow {
        Trace(js.trace(traceName))
    }

    actual fun isPerformanceCollectionEnabled(): Boolean = js.dataCollectionEnabled

    actual fun setPerformanceCollectionEnabled(enable: Boolean) {
        js.dataCollectionEnabled = enable
    }

    fun isInstrumentationEnabled(): Boolean = js.instrumentationEnabled

    fun setInstrumentationEnabled(enable: Boolean) {
        js.instrumentationEnabled = enable
    }
}

actual open class FirebasePerformanceException(code: String, cause: Throwable) :
    FirebaseException(code, cause)

internal inline fun <R> rethrow(function: () -> R): R {
    try {
        return function()
    } catch (e: Exception) {
        throw e
    } catch (e: dynamic) {
        throw errorToException(e)
    }
}

internal fun errorToException(error: dynamic) = (error?.code ?: error?.message ?: "")
    .toString()
    .lowercase()
    .let { code ->
        when {
            else -> {
                println("Unknown error code in ${JSON.stringify(error)}")
                FirebasePerformanceException(code, error)
            }
        }
    }
