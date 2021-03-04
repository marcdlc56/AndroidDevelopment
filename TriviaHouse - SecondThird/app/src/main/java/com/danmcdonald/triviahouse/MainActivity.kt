package com.danmcdonald.triviahouse

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.net.HttpURLConnection
import java.net.URL

const val BACKPACK = "BACKPACK"
const val ADVICE = "ADVICE"
const val OBSERVATION = "OBSERVATION"

class MainActivity : AppCompatActivity() {

    private var playerImageView: ImageView? = null
    private var playerTextView: TextView? = null
    private var yourStatusTextView: TextView? = null
    private var gameStatusTextView: TextView? = null
    private var answerButton: Button? = null
    private var moveButton: Button? = null
    private var giveButton: Button? = null
    private var lookButton: Button? = null
    private var useButton: Button? = null

    private var locationString: String? = null
    private var locationCode: String? = null
    private var newBackPackString: String = ""
    private var backPack: MutableMap<String, Any>? = null
    private var attendant: String? = ""
    private var houseMap: MutableMap<String?, Room?>? = null
    private var currentRoom: Room? = null
    private val roomMap = mapOf("f1r0" to "Front door", "f1r1" to "1st floor, entry room",
        "f1r2" to "1st floor, dining room", "f1r3" to "1st floor, living room",
        "f1r4" to "1st floor, family room", "f1r5" to "1st floor, kitchen")

    //Get a connection
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val houseRef: DatabaseReference = rootRef.child("house")

    private var locationRef: DatabaseReference? = null
    private var backPackRef: DatabaseReference? = null

    //Firebase authentication
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null

    private var userName:String? = null
    private var userPath:String? = null
    private var photoURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser

        if(firebaseUser == null){
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        } else{
            userName = firebaseUser?.email
            userPath = userName?.substring(0, userName?.indexOf("@")!!)
            if(firebaseUser?.photoUrl != null){
                photoURL = firebaseUser?.photoUrl.toString()
            }
            locationRef = rootRef.child("users").child(userPath!!).child("location")
            backPackRef = rootRef.child("users").child(userPath!!).child("backpack")
        }

        playerImageView = findViewById(R.id.playerImageView)
        playerTextView = findViewById(R.id.playerTextView)
        yourStatusTextView = findViewById(R.id.yourStatusTextView)
        gameStatusTextView = findViewById(R.id.gameStatusTextView)
        answerButton = findViewById(R.id.answerButton)
        moveButton = findViewById(R.id.moveButton)
        giveButton = findViewById(R.id.giveButton)
        lookButton = findViewById(R.id.lookButton)
        useButton = findViewById(R.id.useButton)
        backPack = HashMap<String, Any>()
        houseMap = HashMap<String?, Room?>()

        backPack?.put("0", "Crumbl cooke")
        backPack?.put("1", "Sweettooth Fairy cupcake")
        backPackRef?.updateChildren(backPack!!)
        locationRef?.setValue("f1r0")
        playerTextView?.text = userName
        LoadImageTask(playerImageView!!).execute(photoURL)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.sign_out_menu -> {
                firebaseAuth?.signOut()
                userName = ""
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    inner class LoadImageTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {
        //load image; params[0] is the String URL representing the image
        override fun doInBackground(vararg params: String): Bitmap? {
            var bitmap: Bitmap? = null
            var connection: HttpURLConnection? = null
            try {
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection
                try { connection.inputStream.use { inputStream -> bitmap = BitmapFactory.decodeStream(inputStream) } }
                catch (e: Exception) {e.printStackTrace()}
            } catch (e: Exception) { e.printStackTrace()}
            finally { connection!!.disconnect() }
            return bitmap
        }
        override fun onPostExecute(bitmap: Bitmap) {
            imageView.setImageBitmap(bitmap)
        }
    }


    override fun onStart() {
        super.onStart()
        locationRef?.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                locationCode = snapshot.value as String
                locationString = "You are at the ${roomMap[locationCode]}"
                currentRoom = houseMap?.get(locationCode)
                attendant = "A ${currentRoom?.attendant} is in the room"
                yourStatusTextView?.text ="$locationString\n$attendant\n$newBackPackString"
            }
            override fun onCancelled(error: DatabaseError) {
                //if there is an error
            }
        })
        backPackRef?.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                backPack?.clear()
                newBackPackString = ""
                for(item in snapshot.children){
                    backPack?.put(item.key as String, item.value as String)
                    newBackPackString += "Item: ${item.value as String}\n"
                }
                newBackPackString = "Your backpack contains the following items:\n${newBackPackString.trim()}"
                yourStatusTextView?.text ="$locationString\n$attendant\n$newBackPackString"
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        houseRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children){
                    houseMap?.put(item.key, item.getValue(Room::class.java))
                }
                currentRoom = houseMap?.get(locationCode)
                attendant = "A ${currentRoom?.attendant} is in the room"
                yourStatusTextView?.text ="$locationString\n$attendant\n$newBackPackString"
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }


}