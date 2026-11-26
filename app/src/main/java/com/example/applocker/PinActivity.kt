package com.example.applocker

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class PinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val target = intent.getStringExtra("target_pkg").orEmpty()

        if (!Prefs.hasPin(this)) {
            Toast.makeText(this, "Сначала задайте PIN в приложении", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }
        val input = EditText(this).apply {
            hint = "Введите PIN"
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }
        val ok = Button(this).apply { text = "Разблокировать" }
        layout.addView(input)
        layout.addView(ok)
        setContentView(layout)

        ok.setOnClickListener {
            if (Prefs.checkPin(this, input.text.toString())) {
                AppWatchService.unlockedPkg = target
                finish()
                packageManager.getLaunchIntentForPackage(target)?.let { startActivity(it) }
            } else {
                Toast.makeText(this, "Неверный PIN", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        })
    }
}
