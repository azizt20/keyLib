package com.keylibapp.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.keylibapp.R
import com.keylibapp.tools.MyCrypto
import kotlinx.android.synthetic.main.activity_random_password_generate.*

class RandomPasswordGenerate : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_password_generate)
        initData()
    }

    fun initData() {
        copy_generated_password.setOnClickListener {
            if (!show_password_lable.text.toString().isEmpty()) {
                copyToClip("password", show_password_lable.text.toString())
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.random_password_generate_before_generate_password),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        generate_password_btn.setOnClickListener {
            var passwordLength = 0
            if (short_password.isChecked) {
                passwordLength = 8
            } else if (long_password.isChecked) {
                passwordLength = 16
            }

            if (passwordLength == 0) {
                Toast.makeText(
                    this,
                    getString(R.string.random_password_chose_password_length),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val password = MyCrypto().generateRandomKey(passwordLength)
                show_password_lable.setText(password)
            }
        }
    }

    private fun copyToClip(label: String, text: String) {
        var myClipboard =
            ContextCompat.getSystemService(this, ClipboardManager::class.java) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(label, text)
        myClipboard.setPrimaryClip(clip)
        Toast.makeText(
            this,
            getString(R.string.copied_to_clipboard),
            Toast.LENGTH_LONG
        ).show()
    }


}