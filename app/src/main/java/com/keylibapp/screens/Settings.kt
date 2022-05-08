package com.keylibapp.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.keylibapp.R
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        biometrical_switch.isChecked = MainActivity.UserData.hasBioAuth
        appPassword.isChecked = MainActivity.UserData.hasAppCode

        biometrical_switch.setOnClickListener {
            if (!MainActivity.UserData.hasBioAuth && !MainActivity.UserData.hasAppCode) {
                startActivity(
                    Intent(this, PasswordScreen::class.java)
                        .putExtra("mode", "setPasswordAndBioAuth")
                        .putExtra("setBioAuth", true)
                )
                finish()
            } else if (!MainActivity.UserData.hasBioAuth && MainActivity.UserData.hasAppCode) {
                startActivity(
                    Intent(this, PasswordScreen::class.java)
                        .putExtra("mode", "setBioAuth")
                        .putExtra("setBioAuth", true)
                )
                finish()
            } else if (MainActivity.UserData.hasBioAuth && MainActivity.UserData.hasAppCode) {
                startActivity(
                    Intent(this, PasswordScreen::class.java)
                        .putExtra("mode", "setBioAuth")
                        .putExtra("setBioAuth", false)
                )
                finish()
            }
        }

        appPassword.setOnClickListener {
            if (!MainActivity.UserData.hasAppCode) {
                startActivity(
                    Intent(this, PasswordScreen::class.java).putExtra(
                        "mode",
                        "setPassword"
                    )
                )
                finish()
            } else {
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .putExtra("changeStateAppCode", true)
                        .putExtra("stateAppCode", false)
                )
                MainActivity.UserData.hasBioAuth = false
                finish()
            }
        }

        lang_btn.setOnClickListener {
            intent = Intent(this, Language::class.java)
            startActivity(intent)
        }

        account_settings.setOnClickListener {
            intent = Intent(this, UserAccountSettings::class.java)
            startActivity(intent)
        }

        secret_key.setOnClickListener {
            alert()
        }

    }

    private fun alert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.your_encryption_key))
        builder.setMessage(MainActivity.UserData.secretKey)
        builder.setIcon(android.R.drawable.ic_dialog_alert)


        builder.setPositiveButton(getString(R.string.copy)) { dialogInterface, which ->
            copyToClip()
        }

        builder.setNegativeButton(getString(R.string.change_your_key)) { dialogInterface, which ->
            startActivity(Intent(this, ChangeSecretKey::class.java))
            finish()
        }

        builder.setNeutralButton(getString(R.string.cancel)) { dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun copyToClip() {
        var myClipboard =
            ContextCompat.getSystemService(this, ClipboardManager::class.java) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("key", MainActivity.UserData.secretKey)
        myClipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.secret_key_copied), Toast.LENGTH_LONG).show()
    }
}