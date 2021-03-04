package com.marcdelacruz.weatherviewer

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    private val weatherList = ArrayList<Weather>()
    private var weatherListView: ListView? = null
    private var weatherArrayAdapter: WeatherArrayAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        weatherListView = findViewById(R.id.weatherListView)
        weatherArrayAdapter = WeatherArrayAdapter(this, weatherList)
        weatherListView?.adapter = weatherArrayAdapter

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val locationEditText = findViewById<EditText>(R.id.locationEditText)
            val url = createURL(locationEditText.text.toString())
            if(url != null){
                dismissKeyboard(locationEditText)
                val getLocalWeatherTask = GetWeatherTask()
                getLocalWeatherTask.execute(url)
            }
            else{
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        getString(R.string.invalid_url),
                        Snackbar.LENGTH_LONG
                    ).show()
            }
        }
    }

    inner class GetWeatherTask: AsyncTask<URL, Void, JSONObject>(){
        override fun doInBackground(vararg params: URL): JSONObject? {
            var connection: HttpURLConnection? = null
            try {
                connection = params[0].openConnection() as HttpURLConnection
                val response = connection.responseCode
                if(response == HttpURLConnection.HTTP_OK){
                    val builder = StringBuilder()
                    try {
                        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                            reader.forEachLine { line -> builder.append(line) }
                        }
                    } catch (e: IOException){
                        Snackbar.make(
                            findViewById(R.id.coordinatorLayout),
                            getString(R.string.read_error),
                            Snackbar.LENGTH_LONG
                        ).show()
                        e.printStackTrace()
                    }
                    return JSONObject(builder.toString())
                } else{
                    Snackbar.make(
                        findViewById(R.id.coordinatorLayout),
                        getString(R.string.connect_error),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } catch (e: java.lang.Exception){
                Snackbar.make(
                    findViewById(R.id.coordinatorLayout),
                    getString(R.string.connect_error),
                    Snackbar.LENGTH_LONG).show()
                e.printStackTrace()
            } finally {
                connection!!.disconnect()
            }
            return null
        }

        override fun onPostExecute(weather: JSONObject){
            convertJSONtoArrayList(weather)
            weatherArrayAdapter!!.notifyDataSetChanged()
            weatherListView!!.smoothScrollToPosition(0)
        }
    }

    private fun convertJSONtoArrayList(forecast: JSONObject){
        weatherList.clear()
        try{
            println(forecast.toString())
            val list = forecast.getJSONArray("list")

            for (i in 0 until list.length()){
                val day = list.getJSONObject(i)

                val temperatures = day.getJSONObject("main")
                val weather = day.getJSONArray("weather").getJSONObject(0)
                weatherList.add(
                    Weather(
                        day.getLong("dt"),
                            temperatures.getDouble("temp_min"),
                            temperatures.getDouble("temp_max"),
                            temperatures.getDouble("humidity"),
                            weather.getString("description"),
                            weather.getString("icon")


                        )
                    )
            }
        } catch (e: JSONException){
            e.printStackTrace()
        }
    }

    private fun dismissKeyboard (view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun createURL(city: String): URL?{
        val apiKey = getString(R.string.api_key)
        val baseURL = getString(R.string.web_service_url)
        try {
            val urlString = baseURL + URLEncoder.encode(city, "UTF-8") + "&units=imperial&APPID=" + apiKey
            println("URL String: $urlString")
            return URL(urlString)
        } catch (e: Exception){
            e.printStackTrace()
        }

        return null
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}