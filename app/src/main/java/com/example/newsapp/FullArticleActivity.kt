package com.example.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.example.newsapp.databinding.ActivityFullArticleBinding
import com.example.newsapp.models.Item

class FullArticleActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var binding: ActivityFullArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webView = binding.webView


        if (intent.hasExtra("selectedItem")) {
            val selectedItem = intent.getParcelableExtra<Item>("selectedItem")
            webView.loadUrl(selectedItem!!.link!!)
        }

    }
}