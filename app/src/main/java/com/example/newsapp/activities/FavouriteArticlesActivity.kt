package com.example.newsapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.FirestoreClass
import com.example.newsapp.R
import com.example.newsapp.RecyclerAdapter
import com.example.newsapp.RecyclerAdapterFavourites
import com.example.newsapp.databinding.ActivityFavouriteArticlesBinding
import com.example.newsapp.models.Item

class FavouriteArticlesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteArticlesBinding
    private lateinit var rvFavouriteNews: RecyclerView
    private lateinit var firestore: FirestoreClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firestore = FirestoreClass()
        rvFavouriteNews = binding.rvFavouriteNews
        firestore.getUserFavouritesList(this)
    }

    fun setUserFavouritesList(userFavourites: List<Item>) {

        val adapter = RecyclerAdapterFavourites(userFavourites)
        rvFavouriteNews.adapter = adapter
        rvFavouriteNews.layoutManager = LinearLayoutManager(this)
        rvFavouriteNews.setHasFixedSize(true)

    }
}