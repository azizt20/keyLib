package com.keylibapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.keylibapp.R
import com.keylibapp.tools.FirebaseMethodsStorage
import kotlinx.android.synthetic.main.activity_user_account_settings.*

class UserAccountSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account_settings)

        logout.setOnClickListener { showLogOutAlert() }

        deleteaccount.setOnClickListener { showDeleteAccountAlert() }

        change_email.setText(MainActivity.UserData.email)

        save_email_password_changes.setOnClickListener {
            if (virefiEmailAndPassword()) {
                showLoginOrEmailChangingAlert()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun virefiEmailAndPassword(): Boolean {
        if (change_email.text.toString().isEmpty()) {
            Toast.makeText(this, "enter data", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun showLoginOrEmailChangingAlert() {
        val builder = AlertDialog.Builder(this)
        val pass = change_password.text.toString()
        val savedEmail = MainActivity.UserData.email
        builder.setTitle(getString(R.string.are_you_sure))
        builder.setMessage(getString(R.string.that_is_will_change_your_email_and_password))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
            if (!change_email.text.toString().equals(savedEmail)) {
                FirebaseMethodsStorage().updateUserEmail(change_email.text.toString())
                goToMainScreen()
            }
            if (!pass.isEmpty() and (pass.length >= 8)) {
                FirebaseMethodsStorage().updateUserPassword(pass)
            } else {
                Toast.makeText(this, getString(R.string.password_too_short), Toast.LENGTH_LONG)
                    .show()
            }
        }

        builder.setNeutralButton(getString(R.string.cancel)) { dialogInterface, which ->

        }
        builder.setNegativeButton(getString(R.string.no)) { dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showLogOutAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.are_you_sure))
        builder.setMessage(
            getString(R.string.you_need_to_save_your_encryption_key)
                    + "\n ${MainActivity.UserData.secretKey}"
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
            FirebaseMethodsStorage().logUot()
            startActivity(
                Intent(this, MainActivity::class.java)
                    .putExtra("keyDelete", true)
                    .putExtra("deleteAllData", true)
            )
            finish()
        }

        builder.setNeutralButton(getString(R.string.cancel)) { dialogInterface, which ->

        }
        builder.setNegativeButton(getString(R.string.no)) { dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showDeleteAccountAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.are_you_sure))
        builder.setMessage(getString(R.string.that_is_will_delete_your_account_and_all_data))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, which ->
            FirebaseMethodsStorage().deleteUser()

            startActivity(
                Intent(this, MainActivity::class.java)
                    .putExtra("keyDelete", true)
                    .putExtra("deleteAllData", true)
            )
            finish()
        }

        builder.setNeutralButton(getString(R.string.cancel)) { dialogInterface, which ->

        }
        builder.setNegativeButton(getString(R.string.no)) { dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


    fun goToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}