package com.example.newsapp

import android.util.Log
import com.example.newsapp.models.Item
import com.example.newsapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val fireStore = FirebaseFirestore.getInstance()
    private val usersRef = fireStore.collection(Constants.USERS)

    fun registerUser(activity:RegisterActivity, user: User){
        usersRef.document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisterSuccess()
                Log.e(
                    activity.javaClass.simpleName,
                    "Registration successful"
                )
            }
            .addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Error: User registration unsuccessful"
                )
            }

    }

    fun addArticleToFavourites(item:Item){
        val userId = getCurrentUserId()
        val favouritesRef = fireStore.collection(Constants.USERS)
            .document(userId)
            .collection(Constants.FAVOURITES)
            .add(item)
    }

    fun getCurrentUserId():String{
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserId=""
        if(currentUser!=null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
     fun getUserName(userId:String):String{
        var userName:String = ""
        usersRef.document(userId).get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            userName = user?.name.toString()

        }.addOnFailureListener {
            Log.i("getUserName", "Failure")
        }
        return userName
    }

    fun getUserFavouritesList(userId:String):MutableList<Item>{
        var userFavourites:MutableList<Item> = mutableListOf()
       usersRef
            .document(userId)
            .collection(Constants.FAVOURITES)
            .get()
            .addOnSuccessListener{  document ->
                userFavourites = document.toObjects(Item::class.java)
            }
        return userFavourites
    }
}