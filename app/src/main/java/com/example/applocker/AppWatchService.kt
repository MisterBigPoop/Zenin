package com.example.applocker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class AppWatchService : AccessibilityService() {

    companion object {
        @Volatile var unlockedPkg: String? = null
        @Volatile var lastForeground: String? = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        if (pkg == packageName || pkg == "com.android.systemui") return

        lastForeground = pkg

        val locked = Prefs.locked(this)
        if (pkg in locked && unlockedPkg != pkg) {
            val i = Intent(this, PinActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("target_pkg", pkg)
            }
            startActivity(i)
        }
    }

    override fun onInterrupt() {}
}
