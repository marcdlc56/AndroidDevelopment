package com.marcdelacruz.recipebook

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mainFragment: MainActivityFragment = MainActivityFragment()
    var gridFragment: GridFragment = GridFragment()
    var listViewShowing:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.viewFAB).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            super.onCreate(savedInstanceState)
            setSupportActionBar(toolbar)
            setContentView(R.layout.activity_main)

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragmentContainer, mainFragment)
            transaction.commit()

            listViewShowing = true
            viewFAB.setOnClickListener(switchViewButtonClicked)



        }
    }

    private val switchViewButtonClicked: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            val transaction = supportFragmentManager.beginTransaction()
            if (listViewShowing) {
                transaction.replace(R.id.fragmentContainer, gridFragment)
                listViewShowing = false
                viewFAB.setImageResource(R.drawable.ic_baseline_view_list_24)
            } else {
                transaction.replace(R.id.fragmentContainer, mainFragment)
                listViewShowing = true
                viewFAB.setImageResource(R.drawable.ic_view_module_black_24dp)
            }
            transaction.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}