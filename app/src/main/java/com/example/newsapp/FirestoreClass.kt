package com.example.newsapp

import android.util.Log
import com.example.newsapp.activities.FavouriteArticlesActivity
import com.example.newsapp.activities.MainActivity
import com.example.newsapp.activities.RegisterActivity
import com.example.newsapp.models.Item
import com.example.newsapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {
    private val fireStore = FirebaseFirestore.getInstance()


    fun registerUser(activity: RegisterActivity, user: User) {
        fireStore.collection(Constants.USERS).document(user.id!!)
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

    private fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun getCurrentUserData(activity: MainActivity) {
        fireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->

                Log.i("getUserName", document.data.toString())
                val user = document.toObject(User::class.java)!!
                activity.setUserData(user)

            }.addOnFailureListener {
                Log.i("getUserName", "Failure")
            }
    }

    fun getUserFavouritesList(activity: FavouriteArticlesActivity) {
        val userId = getCurrentUserId()
        var userFavourites: MutableList<Item> = mutableListOf()
        fireStore.collection(Constants.USERS)
            .document(userId)
            .collection(Constants.FAVOURITES)
            .get()
            .addOnSuccessListener { document ->
                userFavourites = document.toObjects(Item::class.java)
                activity.setUserFavouritesList(userFavourites)
            }
    }

    fun addArticleToFavourites(item: Item) {
        val userId = getCurrentUserId()
        fireStore.collection(Constants.USERS)
            .document(userId)
            .collection(Constants.FAVOURITES)
            .add(item)
    }

    fun addArticleToRead(link: Item) {
        val userId = getCurrentUserId()
        link?.let {
            fireStore.collection(Constants.USERS)
                .document(userId)
                .collection(Constants.READ)
                .add(it)
        }
    }

    fun getUserReadArticles(activity: MainActivity) {
        var userReadArticles: MutableList<Item> = mutableListOf()
        val userId = getCurrentUserId()
        fireStore.collection(Constants.USERS)
            .document(userId)
            .collection(Constants.READ)
            .get()
            .addOnSuccessListener { document ->
                userReadArticles = document.toObjects(Item::class.java)
                activity.setReadArticles(userReadArticles)
            }
    }
}