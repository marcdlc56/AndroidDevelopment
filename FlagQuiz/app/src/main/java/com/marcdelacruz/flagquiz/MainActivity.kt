package com.marcdelacruz.flagquiz

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

const val CHOICES = "pref_numberOfChoices"
const val REGIONS = "pref_regionsToInclude"

class MainActivity : AppCompatActivity() {
    private var phoneDevice = true //used to force portrait mode
    private var quizFragment: MainFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        androidx.preference.PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(preferencesChangeListener)
        quizFragment = MainFragment()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragmentContainer, quizFragment!!)
        fragmentTransaction.commit()

        val screenSize = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        if(screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
        {
            phoneDevice = false // not a phone-sized device
        }
        if(phoneDevice)
        {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private val preferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == REGIONS) {
            val regions = sharedPreferences.getStringSet(REGIONS, null)
            if (regions == null || regions.size == 0){
                // must select one region--set North America as default
                val editor = sharedPreferences.edit()
                regions!!.add("North_America")
                editor.putStringSet(REGIONS, regions)
                editor.apply()
                Toast.makeText(this@MainActivity,
                R.string.default_region_message, Toast.LENGTH_SHORT).show()
            }
        }
        Toast.makeText(this@MainActivity,
            "Quiz will restart with your new settings", Toast.LENGTH_SHORT).show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val orientation = resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            menuInflater.inflate(R.menu.menu_main, menu)
            true
        } else {
            false
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when (item.itemId){
           R.id.action_settings -> {
               val fragmentTransaction = supportFragmentManager.beginTransaction()
               fragmentTransaction.replace(R.id.fragmentContainer, SettingsFragment())
               fragmentTransaction.addToBackStack(null)
               fragmentTransaction.commit()
               true
           }
           else -> super.onOptionsItemSelected(item)
        }

    }



}