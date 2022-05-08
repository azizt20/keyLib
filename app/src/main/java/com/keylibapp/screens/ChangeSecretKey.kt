package com.keylibapp.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.keylibapp.R
import com.keylibapp.tools.FirebaseMethodsStorage
import com.keylibapp.tools.MyCrypto
import kotlinx.android.synthetic.main.activity_change_secret_key.*

class ChangeSecretKey : AppCompatActivity(), View.OnClickListener {

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_secret_key)

        secret_key_et.setText(MainActivity.UserData.secretKey)
        set_new_secret_key_qwe.setOnClickListener(this)
        generate_password_btn_qwe.setOnClickListener(this)
    }

    private fun reencryptWithNewKey(newKey: String) {
        for (i in MainActivity.UserData.accountsList) {
            FirebaseMethodsStorage().addNewAccount(
                i.service,
                i.login,
                i.password,
                i.comment,
                newKey
            )
        }
    }

    private fun saveNewKey(newKey: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("newKey", newKey)
        startActivity(intent)
        finish()
    }

    private fun generateKey(): String {
        return MyCrypto().generateRandomKey(16)
    }

    private fun changeSecretKey(newKey: String) {
        reencryptWithNewKey(newKey)
        saveNewKey(newKey)
    }

    override fun onClick(v: View?) {
        lateinit var newKey: String
        if (v!!.id == R.id.generate_password_btn_qwe) {
            newKey = generateKey()
        } else {
            newKey = editKey.text.toString()
            Log.i("________", newKey)
            if (newKey.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.change_secret_key_enter_key),
                    Toast.LENGTH_LONG
                ).show()
                return
            } else if (newKey.length != 16) {
                Toast.makeText(
                    this,
                    getString(R.string.change_secret_key_key_length),
                    Toast.LENGTH_LONG
                ).show()
                return
            }

        }
        changeSecretKey(newKey)

    }
}