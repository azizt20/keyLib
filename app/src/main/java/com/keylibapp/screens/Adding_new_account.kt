package com.keylibapp.screens

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.keylibapp.R
import com.keylibapp.tools.FirebaseMethodsStorage
import kotlinx.android.synthetic.main.activity_adding_new_account.*

class Adding_new_account : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_new_account)
        initVars()
    }

    private fun initVars() {

        add_password.setTransformationMethod(PasswordTransformationMethod())

        isChangingMode()

        add_submit_btn.setOnClickListener {
            val qwe = createNewAcccount(
                    service = add_service_name.text.toString(),
                    login = add_login.text.toString(),
                    password = add_password.text.toString(),
                    comment = add_comment.text.toString(),
            )
            if (qwe) {
                startActivity(Intent(this, MainScreen::class.java))
                finish()
            } else {
                Toast.makeText(this, getString(R.string.adding_account_enter_valid_data), Toast.LENGTH_LONG).show()
            }
        }

    }

    fun isChangingMode() {
        try {

            add_service_name.setText(intent.getStringExtra("service"))
            add_login.setText(intent.getStringExtra("email"))
            add_password.setText(intent.getStringExtra("password"))
            add_comment.setText(intent.getStringExtra("comment"))
            if (intent.getStringExtra("service") != null) {
                add_service_name.isEnabled = false
            }

        } catch (e: Exception) {

        }
    }

    private fun createNewAcccount(service: String, login: String, password: String, comment: String): Boolean {
        if (!(service.isEmpty() || login.isEmpty() || password.isEmpty())) {
            if (!(service.contains(".") || service.contains("#")
                            || service.contains("$") || service.contains("[")
                            || service.contains("]"))) {
                var helper = FirebaseMethodsStorage()
                helper.addNewAccount(service, login, password, comment)
                return true
            } else {
                Toast.makeText(this, getString(R.string.adding_account_name_must_not_contain), Toast.LENGTH_LONG).show()
                return false
            }
        } else {
            return false
        }
    }
}





