package com.keylibapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.keylibapp.R
import com.keylibapp.tools.FirebaseMethodsStorage
import kotlinx.android.synthetic.main.activity_password_reset.*

class PasswordReset : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        forgotPassword_send.setOnClickListener {
            val eml = forgotPassword_email.text.toString()
            if (!eml.isEmpty()) {
                FirebaseMethodsStorage().auth.sendPasswordResetEmail(eml)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                goBackToLoginScreen()
                            } else {
                                Toast.makeText(this, getString(R.string.password_reset_error), Toast.LENGTH_LONG).show()
                            }
                        }
            } else {
                Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_LONG).show()
            }
        }

        forgotPassword_cancel.setOnClickListener {
            goBackToLoginScreen()
        }

    }

    private fun goBackToLoginScreen() {
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}