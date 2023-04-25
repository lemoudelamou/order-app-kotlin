package com.example.solubox.network


import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Ein lebenszyklusorientiertes Observable, das nur neue Aktualisierungen nach der Subskription sendet und für Ereignisse wie
 * Navigation und Snackbar-Nachrichten.
 *
 *
 * Dies vermeidet ein häufiges Problem mit Ereignissen: bei Konfigurationsänderungen (wie Rotation) kann eine Aktualisierung
 * kann ein Update ausgesendet werden, wenn der Beobachter aktiv ist. Dieses LiveData ruft das Observable nur auf, wenn es einen
 * expliziten Aufruf von setValue() oder call() gibt.
 *
 *
 * Beachten Sie, dass nur ein Beobachter über Änderungen benachrichtigt wird.
 */
open class SingleLiveEvent<T> : MutableLiveData<T?>() {
    private val mPending = AtomicBoolean(false)
    override fun observe(owner: LifecycleOwner, observer: Observer<in T?>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}