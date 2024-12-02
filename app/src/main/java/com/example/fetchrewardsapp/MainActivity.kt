package com.example.fetchrewardsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val groupedItems = fetchData()
            recyclerView.adapter = ItemsAdapter(groupedItems)
        }
    }

    private suspend fun fetchData(): List<Pair<Int, List<Item>>> {
        val apiUrl = "https://fetch-hiring.s3.amazonaws.com/hiring.json"
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val items = parseItems(response)
                    items
                        .filter { !it.name.isNullOrEmpty() }
                        .groupBy { it.listId }
                        .map { (listId, itemList) ->
                            Pair(listId, itemList.sortedBy { it.id })
                        }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }


    private fun parseItems(jsonResponse: String): List<Item> {
        val items = mutableListOf<Item>()
        val jsonArray = JSONArray(jsonResponse)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val id = jsonObject.optInt("id", -1)
            val listId = jsonObject.optInt("listId", -1)
            val name = jsonObject.optString("name", null)
            if (id != -1 && listId != -1 && !name.isNullOrEmpty()) {
                items.add(Item(id, listId, name))
            }
        }
        return items
    }

}

