package com.example.applocker

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 64, 32, 32)
        }
        setContentView(root)

        root.addView(Button(this).apply {
            text = "Задать/сменить PIN"
            setOnClickListener { askPin() }
        })

        root.addView(Button(this).apply {
            text = "Разрешить оверлей"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
            }
        })

        root.addView(Button(this).apply {
            text = "Включить службу доступности"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        })

        root.addView(Button(this).apply {
            text = "Выбрать приложения для блокировки"
            setOnClickListener { chooseApps() }
        })
    }

    private fun askPin() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        AlertDialog.Builder(this)
            .setTitle("Новый PIN (4+ цифры)")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val pin = input.text.toString()
                if (pin.length >= 4) {
                    Prefs.setPin(this, pin)
                } else {
                    android.widget.Toast.makeText(this, "Слишком короткий PIN", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun chooseApps() {
        val pm = packageManager
        val apps = pm.getInstalledApplications(0)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { pm.getApplicationLabel(it).toString() }

        val names = apps.map { pm.getApplicationLabel(it).toString() }.toTypedArray()
        val current = Prefs.locked(this).toMutableSet()
        val checked = BooleanArray(apps.size) { apps[it].packageName in current }

        AlertDialog.Builder(this)
            .setTitle("Заблокировать")
            .setMultiChoiceItems(names, checked) { _, which, isChecked ->
                val pkg = apps[which].packageName
                if (isChecked) current.add(pkg) else current.remove(pkg)
            }
            .setPositiveButton("Сохранить") { _, _ ->
                Prefs.setLocked(this, current)
            }
            .show()
    }
}
