package com.example.newsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import com.example.newsapp.FirestoreClass
import com.example.newsapp.databinding.ActivityFullArticleBinding
import com.example.newsapp.models.Item

class FullArticleActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var binding: ActivityFullArticleBinding
    private lateinit var btnAddToFavourites: Button
    private lateinit var btnShare: Button
    private var item: Item? = null
    private lateinit var firestore: FirestoreClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webView = binding.webView
        firestore = FirestoreClass()


        if (intent.hasExtra("selectedItem")) {
            item = intent.getParcelableExtra<Item>("selectedItem")
            webView.loadUrl(item!!.link!!)
        }
        btnAddToFavourites = binding.btnAddToFavourites
        btnAddToFavourites.setOnClickListener() {
            firestore.addArticleToFavourites(item!!)
            Toast.makeText(this, "Article added to favourites", Toast.LENGTH_SHORT).show()
        }
        btnShare = binding.btnShare
        btnShare?.setOnClickListener() { view ->

            var sendIntent: Intent = Intent();
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT, item.toString()
            )
            sendIntent.type = "text/plain"
            var shareIntent: Intent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }


    }
}