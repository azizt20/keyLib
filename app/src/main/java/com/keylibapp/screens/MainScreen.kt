package com.keylibapp.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.keylibapp.AccountModel
import com.keylibapp.R
import com.keylibapp.tools.ExpandableListAdapter
import com.keylibapp.tools.FirebaseMethodsStorage
import com.keylibapp.tools.MyCrypto
import kotlinx.android.synthetic.main.activity_main_screen.*

class MainScreen : AppCompatActivity() {

    val accountsList: MutableList<AccountModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

//        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE)
        initVars()
        getDataFromFirebase()
        creaateBottonSheet()
    }

    private fun initVars() {
        progressBar.visibility = View.VISIBLE

        search.addTextChangedListener {
            val str = search.text.toString()

            val newList: MutableList<AccountModel> = ArrayList()

            for (i in MainActivity.UserData.accountsList) {
                if (i.service.contains(str)) {
                    newList.add(i)
                }
            }

            val accountsHeader: MutableList<String> = ArrayList()
            for (i in newList) {
                accountsHeader.add(i.service)
            }
            expandableListView.setAdapter(ExpandableListAdapter(this, accountsHeader, newList))
        }

        add_new_account_btn.setOnClickListener {
            startActivity(Intent(this, Adding_new_account::class.java))
        }

        settings_btn.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }


        generate_password_btn.setOnClickListener {
            startActivity(Intent(this, RandomPasswordGenerate::class.java))
        }


    }

    private fun getDataFromFirebase() {
        val firebase = FirebaseMethodsStorage()
        val userId = MainActivity.UserData.id
        firebase.databaseReference.child("users").child(userId).get().addOnSuccessListener {
            try {
                MainActivity.UserData.accountsList.clear()
                Log.i("firebase", "Got value ${it.value}")
                if (it.value != null) {
                    val myMap = it.child("accounts").value as HashMap<String, HashMap<*, *>>
                    val crypto = MyCrypto()
                    for (key in myMap.keys) {
                        val service = myMap.get(key)?.get("service").toString()
                        val login = crypto.decrypt(myMap.get(key)?.get("email").toString())
                        val password = crypto.decrypt(myMap.get(key)?.get("password").toString())
                        val comment = crypto.decrypt(myMap.get(key)?.get("comment").toString())
                        accountsList.add(AccountModel(service!!, login!!, password!!, comment!!))
                        MainActivity.UserData.accountsList.add(AccountModel(service!!, login!!, password!!, comment!!))
                    }
                    val accountsHeader: MutableList<String> = ArrayList()
                    for (i in 0..accountsList.size - 1) {
                        accountsHeader.add(accountsList[i].service)
                    }
                    progressBar.visibility = View.GONE
                    expandableListView.setAdapter(ExpandableListAdapter(this, accountsHeader, accountsList))
                }
            } catch (e: Exception) {

            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        firebase.databaseReference.child("users").child(userId).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        firebase.databaseReference.child("users").child(userId).addChildEventListener(firebase.childEventListener)
    }

    private fun creaateBottonSheet() {
        BottomSheetBehavior.from(findViewById(R.id.sheet)).apply {
            setPeekHeight(150)
            this.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }



}