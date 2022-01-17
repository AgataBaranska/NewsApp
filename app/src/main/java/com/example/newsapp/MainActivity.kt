package com.example.newsapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.models.Item
import com.google.firebase.auth.FirebaseAuth
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), RecyclerAdapter.OnItemClickedListener {


    private lateinit var binding: ActivityMainBinding
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())
    private lateinit var rvNews: RecyclerView
    private val FEED_WORLD_URL = "https://feeds.skynews.com/feeds/rss/world.xml"
    private val FEED_US_URL = "https://feeds.skynews.com/feeds/rss/us.xml"
    private lateinit var items: MutableList<Item>
    private var location: Location? = null
    private var gpsLocation: Location? = null
    private var networkLocation: Location? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var userCountryCode: String? = null
    private lateinit var tvId: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnlogOut: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rvNews = binding.rvNews
        items = mutableListOf()

        val userId = intent.getStringExtra("userId")
        val emailId = intent.getStringExtra("emailId")

        tvId = binding.tvUserId
        tvEmail = binding.tvUserEmail
        tvId.text = userId
        tvEmail.text = emailId

        btnlogOut = binding.btnLogOut

        btnlogOut.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        findCountryCode()
        parseDataInBackground()


    }

    private fun findCountryCode() {
        val locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (gpsLocation != null) {
            location = gpsLocation
            longitude = location!!.longitude
            latitude = location!!.latitude
        } else {
            latitude = 0.0
            longitude = 0.0
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
            1
        )
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude!!, longitude!!, 1)
        if (!addresses.isNullOrEmpty()) {
            userCountryCode = addresses[0].countryCode
        }else{
            userCountryCode = "Unknown"
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
            if(userCountryCode =="USA"){

                xmlParser.setInput(downloadUrl(FEED_US_URL), "UTF_8")
            }else{
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
        rvNews.adapter!!.notifyItemChanged(position)
        startActivity(intent)

    }
}