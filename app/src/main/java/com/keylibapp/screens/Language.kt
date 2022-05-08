package com.keylibapp.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.keylibapp.R
import kotlinx.android.synthetic.main.activity_language.*
import java.util.*

class Language : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        showCurrentLocale()
        lang_ru.setOnClickListener { changeLocale("ru") }
        lang_en.setOnClickListener { changeLocale("en") }
        lang_kaa.setOnClickListener { changeLocale("kaa") }
    }

    private fun showCurrentLocale() {
        val current: Locale = resources.configuration.locale
        print(current)
        Log.i("lang", current.toString())
        if (current.toString() == "ru_ru" || current.toString()=="ru") {
            lang_ru.isChecked = true
        }else if(current.toString() == "kaa"){
            lang_kaa.isChecked = true
        } else {
            lang_en.isChecked = true
        }
    }

    private fun changeLocale(locale: String) {
        startActivity(
            Intent(this, MainActivity::class.java).putExtra(
                "locale",
                locale
            )
        )
    }
}