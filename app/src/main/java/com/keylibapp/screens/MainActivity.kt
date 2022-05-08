package com.keylibapp.screens

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.keylibapp.AccountModel
import com.keylibapp.R
import com.keylibapp.tools.FirebaseMethodsStorage
import com.keylibapp.tools.MyCrypto
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tryToEnableOfflineMode()
        chekExtras()
        setVars()
    }

    private fun setVars() {

        val locale = getLocale()
        if( locale !="null"){
            setAppLocale(locale,this)
        }

        progressBar2.visibility = View.GONE

        forgot_password.setOnClickListener {
            startActivity(Intent(this, PasswordReset::class.java))
        }

        btn_sign_up.setOnClickListener {
            startActivity(Intent(this, RegistrationAct::class.java))
        }

        btn_sign_in.setOnClickListener {
            progressBar2.visibility = View.VISIBLE

            GlobalScope.launch(Dispatchers.IO) {
                trySignIn()
            }
        }
    }

    private fun tryToEnableOfflineMode() {
        try {
            Firebase.database.setPersistenceEnabled(true)
        } catch (e: Exception) {
            Log.i("Firebase offline state", "Offline mode is disable")
        }
    }

    private fun chekExtras() {
        val isKeyDelete = intent.getBooleanExtra("keyDelete", false)
        val changeSecretKey = intent.getStringExtra("newKey")
        val currentUser = FirebaseMethodsStorage().auth.currentUser
        val deleteAllData = intent.getBooleanExtra("deleteAllData", false)
        val changeStateAppCode = intent.getBooleanExtra("changeStateAppCode", false)
        val stateAppCode = intent.getBooleanExtra("stateAppCode", false)
        val locale = intent.getStringExtra("locale")

        if(locale != null){
            setLocal(locale)
        }

        if (changeStateAppCode) {
            setAppHasPassword(stateAppCode)
            UserData.hasAppCode = stateAppCode
        }

        if (deleteAllData) {
            DELETE_ALL()
        }

        if (isKeyDelete) {
            setSecretKey("null")
        }

        if (changeSecretKey != null) {
            setSecretKey(changeSecretKey)
        }

        if (currentUser != null) {
            init()
        }
    }

    private fun DELETE_ALL() {
        setSecretKey("null")
        setAppHasPassword(false)
    }

    private suspend fun trySignIn() {
        if (editTextLogin.text.toString().isEmpty()) {
            runOnUiThread {
                editTextLogin.setText("")
                editTextLogin.requestFocus()
                progressBar2.visibility = View.GONE
            }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(editTextLogin.text.toString()).matches()) {
            runOnUiThread {
                editTextLogin.setText(getString(R.string.enter_email))
                editTextLogin.requestFocus()
                progressBar2.visibility = View.GONE
            }
            return
        }

        if (editTextPassword.text.isEmpty()) {
            runOnUiThread {
                editTextPassword.setText("")
                editTextLogin.setText(getString(R.string.enter_password))
                editTextLogin.requestFocus()
                progressBar2.visibility = View.GONE
            }
            return
        }

        val user = FirebaseMethodsStorage().authUser(
            editTextLogin.text.toString(),
            editTextPassword.text.toString()
        )
        Log.i("user info", user.toString())
        updateUI(user)

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            if (user.isEmailVerified) {
                val getAppHasPasswordState = getAppHasPassword()
                Log.d("getAppHasPasswordState", getAppHasPasswordState.toString())
                if (getAppHasPasswordState) {
                    startActivity(
                        Intent(context, PasswordScreen::class.java).putExtra(
                            "mode",
                            "auth"
                        )
                    )
                    finish()
                } else {
                    startActivity(Intent(context, MainScreen::class.java))
                    finish()
                }
            } else {
                runOnUiThread {
                    progressBar2.visibility = View.GONE
                }
                Toast.makeText(
                    context,
                    getString(R.string.main_activity_verify_your_email_address),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            runOnUiThread {
                progressBar2.visibility = View.GONE
                Toast.makeText(
                    context,
                    getString(R.string.main_activity_invalid_login),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onBackPressed() {
        linear_layout.requestFocus()
    }

    fun init() {
        UserData.email = FirebaseMethodsStorage().auth.currentUser.email
        UserData.id = FirebaseMethodsStorage().auth.currentUser.uid
        if (getSrcretKey().equals("null")) {
            setSecretKey(MyCrypto().generateRandomKey(16))
        }
        UserData.secretKey = getSrcretKey()
        updateUI(FirebaseMethodsStorage().auth.currentUser)
    }

    fun setSecretKey(key: String) {
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("key", key)
            apply()
        }
    }

    fun getSrcretKey(): String {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString("key", "null").toString()
    }

    fun setAppHasPassword(state: Boolean) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("hasPassword", state)
            apply()
        }
    }

    fun getAppHasPassword(): Boolean {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getBoolean("hasPassword", false)
    }

    private fun setLocal(locale: String) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("locale", locale)
            apply()
        }
    }

    private fun getLocale(): String{
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString("locale", "null").toString()
    }

    object UserData {
        var email: String = ""
        var id: String = ""
        var secretKey: String = ""
        var hasAppCode = false
        var hasBioAuth = false
        var accountsList: MutableList<AccountModel> = ArrayList()

        init {
            println("UserData is colled")
        }
    }

    fun setAppLocale(languageFromPreference: String?, context: Context) {
        if (languageFromPreference != null) {
            val resources: Resources = context.resources
            val dm: DisplayMetrics = resources.displayMetrics
            val config: Configuration = resources.configuration
            config.setLocale(Locale(languageFromPreference.toLowerCase(Locale.ROOT)))
            resources.updateConfiguration(config, dm)
        }
    }

}