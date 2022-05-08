package com.keylibapp.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.keylibapp.R
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationAct : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener {
            signUp()
        }

    }

    private fun signUp() {
        if (reg_login.text.toString().isEmpty()) {
            reg_login.setText(getString(R.string.enter_email))
            reg_login.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(reg_login.text.toString()).matches()) {
            reg_login.setText(getString(R.string.enter_valid_email))
            reg_login.requestFocus()
            return
        }

        if (reg_password.text.toString() != reg_password_2.text.toString()) {
            reg_password.setText("")
            reg_password_2.setText("")
            reg_login.setText(getString(R.string.your_passwords_is_not_equal))
            reg_login.requestFocus()
            return
        }

        if (reg_password.text.isEmpty()) {
            reg_login.setText(getString(R.string.enter_password))
            reg_login.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(reg_login.text.toString(), reg_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            Toast.makeText(
                                this,
                                getString(R.string.a_link_for_verification_has_been_sent_to_your_email),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, getString(R.string.creation_account_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}