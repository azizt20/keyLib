package com.keylibapp.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.keylibapp.R
import kotlinx.android.synthetic.main.activity_password_screen.*
import java.util.concurrent.Executor

class PasswordScreen : AppCompatActivity(), View.OnClickListener {

    private var code = ""
    private lateinit var mode: String
    private var setBioAuth: Boolean = false
    private var setBioAuthNow: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_screen)

        setVals()


        setBioAuth = intent.getBooleanExtra("setBioAuth", false)
        mode = intent.getStringExtra("mode").toString()
        if (mode == "setPassword") {
            tv_message.text = getString(R.string.password_screen_set_app_password)
        } else if (mode == "auth") {
            tv_message.text = getString(R.string.password_screen_enter_app_password)
        } else if (mode == "setBioAuth") {
            setBioAuthState(setBioAuth)
            mode == "auth"
            if (!setBioAuth) {
                startActivity(Intent(this, MainScreen::class.java))
                finish()
            }
            tv_message.text = getString(R.string.password_screen_enter_app_password)
        } else if (mode == "setPasswordAndBioAuth") {
            mode = "setPassword"
            tv_message.text = getString(R.string.password_screen_set_app_password)
            setBioAuthNow = true
        }

        if (getBioAuthState() && mode != "setPassword") {
            createBioAuth()
        }


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.num0 -> {
                code += "0"
            }
            R.id.num1 -> {
                code += "1"
            }
            R.id.num2 -> {
                code += "2"
            }
            R.id.num3 -> {
                code += "3"
            }
            R.id.num4 -> {
                code += "4"
            }
            R.id.num5 -> {
                code += "5"
            }
            R.id.num6 -> {
                code += "6"
            }
            R.id.num7 -> {
                code += "7"
            }
            R.id.num8 -> {
                code += "8"
            }
            R.id.num9 -> {
                code += "9"
            }
            R.id.numDel -> {
                code = code.dropLast(1)
            }
            R.id.password_submit_btn -> {
                submit()
            }

        }

        password_inputed.setText("*".repeat(code.length))
        Log.d("code", code)
    }

    private fun submit() {


        if (mode == "setPassword") {
            if (code.length >= 4) {
                setPassword(code)
                sendHasPasswordState(true)
                if (setBioAuthNow) {
                    setBioAuthState(true)
                } else {
                    setBioAuthState(false)
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.password_screen_code_to_short),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (mode == "auth" && code == getPassword()) {
            startActivity(Intent(this, MainScreen::class.java))
            finish()
        } else if (mode == "auth" && code != getPassword()) {
            Toast.makeText(this, getString(R.string.password_screen_code_wrong), Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun sendHasPasswordState(state: Boolean) {
        startActivity(
            Intent(this, MainActivity::class.java)
                .putExtra("changeStateAppCode", true)
                .putExtra("stateAppCode", state)
        )
        finish()
    }

    private fun setVals() {

        MainActivity.UserData.hasAppCode = (getPassword() != "null")
        MainActivity.UserData.hasBioAuth = getBioAuthState()

        password_submit_btn.setOnClickListener(this)
        num0.setOnClickListener(this)
        num1.setOnClickListener(this)
        num2.setOnClickListener(this)
        num3.setOnClickListener(this)
        num4.setOnClickListener(this)
        num5.setOnClickListener(this)
        num6.setOnClickListener(this)
        num7.setOnClickListener(this)
        num8.setOnClickListener(this)
        num9.setOnClickListener(this)
        numDel.setOnClickListener(this)

    }

    fun setPassword(pass: String) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("pass", pass)
            apply()
        }
    }

    fun getPassword(): String {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString("pass", "null").toString()

    }

    private fun showBioAuth(executor: Executor) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.password_screen_fingerprint_login))
            .setDeviceCredentialAllowed(true)
            .build()

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    startActivity(Intent(applicationContext, MainScreen::class.java))
                    finish()

                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    fun createBioAuth() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                showBioAuth(executor)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(
                    this,
                    "1)",
                    Toast.LENGTH_LONG
                ).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(
                    this,
                    "2",
                    Toast.LENGTH_LONG
                ).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(
                    this,
                    "3",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    fun setBioAuthState(state: Boolean) {
        MainActivity.UserData.hasBioAuth = state
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("biometricAuth", state)
            apply()
        }
    }

    fun getBioAuthState(): Boolean {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getBoolean("biometricAuth", false)

    }

}