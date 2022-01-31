package com.example.newsapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.FirestoreClass
import com.example.newsapp.RecyclerAdapter
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.models.Item
import com.example.newsapp.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

private const val FEED_WORLD_URL = "https://feeds.skynews.com/feeds/rss/world.xml"
private const val FEED_US_URL = "https://feeds.skynews.com/feeds/rss/us.xml"

class MainActivity : AppCompatActivity(), RecyclerAdapter.OnItemClickedListener {


    private lateinit var binding: ActivityMainBinding
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    private lateinit var rvNews: RecyclerView
    private lateinit var items: MutableList<Item>

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var userCountryCode: String
    private lateinit var userName: TextView

    private lateinit var btnLogOut: Button
    private lateinit var btnFavourites: Button

    private lateinit var firestore: FirestoreClass
    private lateinit var currentUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirestoreClass()
        firestore.getCurrentUserData(this)
        firestore.getUserReadArticles(this)
        userName = binding.tvUserName
        rvNews = binding.rvNews
        items = mutableListOf()
        btnLogOut = binding.btnLogOut
        btnLogOut.setOnClickListener() {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnFavourites = binding.btnFavourites
        btnFavourites.setOnClickListener() {
            startActivity(Intent(this, FavouriteArticlesActivity::class.java))
        }

    }

    override fun onStart() {
        super.onStart()
        userCountryCode = findCountryCode()
        parseDataInBackground()

    }


    private fun findCountryCode(): String {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
                1
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                latitude = location?.latitude
                longitude = location?.longitude
            }

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: MutableList<Address>
        if (latitude != null && longitude != null) {
            addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
        } else {
            latitude = 0.0
            longitude = 0.0
            addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
        }
        if (!addresses.isNullOrEmpty()) {
            return addresses[0].countryCode
        } else {
            return "Unknown"
        }
    }

    private fun downloadUrl(urlString: String): InputStream? {
        val url = URL(urlString)
        return url.openConnection().getInputStream()
    }

    private fun parseDataInBackground() {

        myExecutor.execute {

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val xmlParser = factory.newPullParser()

            println(userCountryCode)
            if (userCountryCode == "US") {
                xmlParser.setInput(downloadUrl(FEED_US_URL), "UTF_8")
            } else {
                xmlParser.setInput(downloadUrl(FEED_WORLD_URL), "UTF_8")
            }

            var insideItem = false
            var eventType = xmlParser.eventType
            var title: String? = null
            var img: String? = null
            var description: String? = null
            var link: String? = null
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {
                    if (xmlParser.name.equals("item")) {
                        insideItem = true
                    } else if (xmlParser.name.equals("title") && insideItem) {
                        title = xmlParser.nextText()
                    } else if (xmlParser.name.equals("media:content") && insideItem) {
                        img = xmlParser.getAttributeValue(null, "url")
                    } else if (xmlParser.name.equals("description") && insideItem) {
                        description = xmlParser.nextText()
                    } else if (xmlParser.name.equals("link") && insideItem) {
                        link = xmlParser.nextText()
                    }

                } else if (eventType == XmlPullParser.END_TAG && xmlParser.name.equals("item")) {
                    val item = Item(title, img, description, link, "UNREAD")
                    items.add(item)
                    insideItem = false
                    link = null
                    img = null
                    description = null
                    title = null
                }
                eventType = xmlParser.next()
            }
            myHandler.post {
                val adapter = RecyclerAdapter(items, this)
                rvNews.adapter = adapter
                rvNews.layoutManager = LinearLayoutManager(this)
                rvNews.setHasFixedSize(true)
            }
        }
    }


    override fun onItemClicked(position: Int) {
        val clickedItem = items[position]
        val intent = Intent(this, FullArticleActivity::class.java)
        intent.putExtra("selectedItem", clickedItem)
        clickedItem.state = "READ"
        firestore.addArticleToRead(clickedItem)
        rvNews.adapter!!.notifyItemChanged(position)
        startActivity(intent)
    }

    fun setUserData(user: User) {
        currentUser = user
        userName.text = currentUser.name
    }

    fun setReadArticles(userReadArticles: List<Item>) {
        for (item in items) {
            for (readItem in userReadArticles) {
                if (item.link.equals(readItem.link)) {
                    item.state = "READ"
                    rvNews.adapter!!.notifyItemChanged(items.indexOf(item))
                }
            }
        }

    }
}