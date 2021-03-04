package com.danmcdonald.triviahouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

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
    private var backPack: MutableList<String>? = null
    private var attendant: String? = ""
    private var houseMap: MutableMap<String?, Room?>? = null
    private var currentRoom: Room? = null
    private val roomMap = mapOf("f1r0" to "Front door", "f1r1" to "First floor, entry room",
        "f1r2" to "First floor, dining room", "f1r3" to "First floor, living room",
        "f1r4" to "First floor, family room", "f1r5" to "First floor, kitchen")

    //Get a connection
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val locationRef: DatabaseReference = rootRef.child("location")
    private val backPackRef: DatabaseReference = rootRef.child("backpack")
    private val houseRef: DatabaseReference = rootRef.child("house")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        playerTextView = findViewById(R.id.playerTextView)
        yourStatusTextView = findViewById(R.id.yourStatusTextView)
        gameStatusTextView = findViewById(R.id.gameStatusTextView)
        answerButton = findViewById(R.id.answerButton)
        moveButton = findViewById(R.id.moveButton)
        giveButton = findViewById(R.id.giveButton)
        lookButton = findViewById(R.id.lookButton)
        useButton = findViewById(R.id.useButton)
        backPack = ArrayList()
        houseMap = HashMap<String?, Room?>()
    }

    override fun onStart() {
        super.onStart()
        locationRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                locationCode = snapshot.value as String
                locationString = "You are at the ${roomMap[locationCode]}"
                yourStatusTextView?.text ="$locationString\n$attendant\n$newBackPackString"
            }
            override fun onCancelled(error: DatabaseError) {
                //if there is an error
            }
        })
        backPackRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                backPack?.clear()
                newBackPackString = ""
                for(item in snapshot.children){
                    backPack?.add(item.value as String)
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