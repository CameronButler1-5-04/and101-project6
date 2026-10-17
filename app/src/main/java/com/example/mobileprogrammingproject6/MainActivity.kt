package com.example.mobileprogrammingproject6

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestHeaders
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {

    private lateinit var petList: MutableList<String>
    private lateinit var rvPets: RecyclerView
    private lateinit var adapter: PetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPets = findViewById(R.id.pet_list)
        petList = mutableListOf()

        adapter = PetAdapter(petList)
        rvPets.adapter = adapter
        rvPets.layoutManager = LinearLayoutManager(this)

        fetchHamsterImages()
    }

    private fun fetchHamsterImages() {
        val client = AsyncHttpClient()

        val url = "https://commons.wikimedia.org/w/api.php" +
                "?action=query&generator=search&gsrsearch=hamster%20filetype:bitmap" +
                "&gsrnamespace=6&gsrlimit=20&prop=imageinfo&iiprop=url&iiurlwidth=600&format=json"

        val requestHeaders = RequestHeaders()
        requestHeaders["User-Agent"] = "RandomPetsLab/1.0 (student project; Android)"

        client.get(url, requestHeaders, RequestParams(), object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.d("Hamster Success", "$json")

                val pages = json.jsonObject.getJSONObject("query").getJSONObject("pages")
                val keys = pages.keys()
                while (keys.hasNext()) {
                    val page = pages.getJSONObject(keys.next())
                    val info = page.getJSONArray("imageinfo").getJSONObject(0)
                    petList.add(info.getString("thumburl"))
                }
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Hamster Error", "$statusCode $errorResponse")
                Toast.makeText(
                    this@MainActivity,
                    "Failed to load images ($statusCode)",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}