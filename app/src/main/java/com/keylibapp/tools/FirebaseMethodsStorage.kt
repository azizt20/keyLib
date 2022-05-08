package com.keylibapp.tools

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.keylibapp.screens.MainActivity
import kotlinx.coroutines.delay
import kotlin.properties.Delegates



class FirebaseMethodsStorage {
    var myDatabase = Firebase.database
    val auth = FirebaseAuth.getInstance()
    val databaseReference = myDatabase.reference



    val childEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d("TAG", "onChildAdded:" + dataSnapshot.key!!)
            val comment = dataSnapshot.getValue()
            Log.i("data", comment.toString())
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d("TAG", "onChildChanged: ${dataSnapshot.key}")
            val newComment = dataSnapshot.getValue()
            val commentKey = dataSnapshot.key


            Log.d("TAG", newComment.toString())

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            Log.d("TAG", "onChildRemoved:" + dataSnapshot.key!!)
            val commentKey = dataSnapshot.key
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
            Log.d("TAG", "onChildMoved:" + dataSnapshot.key!!)
            val movedComment = dataSnapshot.getValue()
            val commentKey = dataSnapshot.key
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("TAG", "postComments:onCancelled", databaseError.toException())
        }
    }

    fun addNewAccount(service: String, email: String, password: String, comment: String, secret_key: String = MainActivity.UserData.secretKey) {
        val crypto = MyCrypto()
        val database = Firebase.database
        val myRef = database.getReference("users/${MainActivity.UserData.id}/accounts/${service}/")
        myRef.child("email").setValue(crypto.encrypt(email, secret_key))
        myRef.child("password").setValue(crypto.encrypt(password, secret_key))
        myRef.child("comment").setValue(crypto.encrypt(comment, secret_key))
        myRef.child("service").setValue(service)

        myRef.push()

    }

    suspend fun authUser(email: String, password: String, counter: Int = 0): FirebaseUser? {
        var user : FirebaseUser?
        user = null
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if(task.isSuccessful){
                        user = auth.currentUser
                        Log.i("user auth","is successful")
                    }else{
                        Log.i("user auth","is not successful")
                    }
                }

        if(user==null && counter < 2){
            delay(3000)
            user = authUser(email,password,counter+1)
        }
        return auth.currentUser
    }

    fun updateUserPassword(newPassword: String) { // обновляет пароль пользователя
        val user = auth.currentUser
        user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("update user`s password", "User password updated.")
                    }
                }
    }

    fun sendPasswordResetEmail(email: String): Boolean { // отправляет сообщение для смены пароля
        var res by Delegates.notNull<Boolean>()
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                        res = task.isSuccessful
                }

        return res
    }

    fun updateUserEmail(newEmail: String) {
        auth.currentUser!!.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("updating user`s email ", "User email address updated.")
                    }
                }
    }

    fun deleteUser() { // удаляет пользователя
        val user = auth.currentUser!!
        user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("account deleted", "User account deleted.")
                    }
                }
    }

    fun logUot() { // выйти из аккаунта
        auth.signOut()
    }


}