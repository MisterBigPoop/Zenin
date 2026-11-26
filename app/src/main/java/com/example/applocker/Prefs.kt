package com.example.applocker

import android.content.Context
import java.security.MessageDigest

object Prefs {
    private const val NAME = "locker_prefs"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_LOCKED = "locked_pkgs"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun setPin(ctx: Context, pin: String) {
        sp(ctx).edit().putString(KEY_PIN_HASH, sha256(pin)).apply()
    }

    fun checkPin(ctx: Context, pin: String): Boolean =
        sp(ctx).getString(KEY_PIN_HASH, null) == sha256(pin)

    fun hasPin(ctx: Context): Boolean = sp(ctx).contains(KEY_PIN_HASH)

    fun setLocked(ctx: Context, pkgs: Set<String>) {
        sp(ctx).edit().putStringSet(KEY_LOCKED, pkgs).apply()
    }

    fun locked(ctx: Context): Set<String> =
        sp(ctx).getStringSet(KEY_LOCKED, emptySet()) ?: emptySet()

    private fun sha256(s: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(s.toByteArray())
            .joinToString("") { "%02x".format(it) }
}
