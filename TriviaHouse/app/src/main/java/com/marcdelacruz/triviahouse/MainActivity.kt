package com.marcdelacruz.triviahouse

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
    private val roomMap = mapOf("f1r0" to "Front door", "f1r1" to "First floor, entry room",
        "f1r2" to "First floor, dining room", "f1r3" to "First floor, living room",
        "f1r4" to "First floor, family room", "f1r5" to "First floor, kitchen")

    //Get a connection
    private val rootRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val houseRef: DatabaseReference = rootRef.child("house")


    private var locationRef: DatabaseReference? = null
    private var backPackRef: DatabaseReference? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null

    private var userName:String? = null
    private var userPath:String? = null
    private var photoUrl:String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.currentUser

        if(firebaseUser == null){
           startActivity(Intent(this, SigninActivity::class.java))
            finish()
        } else{
            userName = firebaseUser?.email
            userPath = userName?.substring(0,userName?.indexOf("@")!!)
            if (firebaseUser?.photoUrl != null){
                photoUrl = firebaseUser?.photoUrl.toString()
            }
            locationRef = rootRef.child("users").child(userPath!!).child("location")
            backPackRef = rootRef.child("users").child(userPath!!).child("backpack")

        }


        playerImageView = findViewById(R.id.playerImageView)
        playerTextView = findViewById(R.id.playerTextView)
        yourStatusTextView = findViewById(R.id.yourStatusTextView)
        yourStatusTextView?.movementMethod = ScrollingMovementMethod()
        gameStatusTextView = findViewById(R.id.gameStatusTextView)
        answerButton = findViewById(R.id.answerButton)
        moveButton = findViewById(R.id.moveButton)
        giveButton = findViewById(R.id.giveButton)
        lookButton = findViewById(R.id.lookButton)
        useButton = findViewById(R.id.useButton)
        backPack = HashMap<String, Any>()
        houseMap = HashMap<String?, Room?>()

        backPack?.put("0", "Crumbl cookie")
        backPack?.put("1", "Sweettooth Fairy cupcake")
        backPackRef?.updateChildren(backPack!!)
        locationRef?.setValue("f1r0")
        playerTextView?.text = userName
        //LoadImageTask(playerImageView!!).execute(photoUrl)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.sign_out_menu -> {
                firebaseAuth?.signOut()
                userName = ""
                startActivity(Intent(this, SigninActivity::class.java))
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

        moveButton?.setOnClickListener(View.OnClickListener { _ ->
            var rooms: Array<String>? = currentRoom?.getMoveToRooms()
            var fullNameRooms: MutableList<String?> = ArrayList()
            for( room in rooms!!){
                fullNameRooms.add(roomMap[room])

            }
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Click on the room to enter")
            builder.setItems(fullNameRooms.toTypedArray()) { _, which ->
                locationRef?.setValue(rooms[which])
            }
            val dialog = builder.create()
            dialog.show()
        })

        lookButton?.setOnClickListener(View.OnClickListener { _ ->
            val look = currentRoom?.getLookObject()
            if(look?.resultType == BACKPACK){
                if(backPack?.containsValue(look.result!!)!!){
                    Toast.makeText(this, "You already have a ${look.result} in your backpack!", Toast.LENGTH_LONG).show()
                } else{
                    backPack?.put("" + backPack?.size, look.result!!)
                    val bp:MutableMap<String, Any> = backPack!!
                    backPackRef?.updateChildren(bp)
                    Toast.makeText(this, "You find a ${look.result}, that is added to your backpack!", Toast.LENGTH_LONG).show()
                }
            } else if( look?.resultType == OBSERVATION){
                Toast.makeText(
                    this,"${look.result}", Toast.LENGTH_LONG
                ).show()
            }

        })

        answerButton?.setOnClickListener(View.OnClickListener { _ ->
            var question: Question? = currentRoom?.getQuestionObject()
            val builder = AlertDialog.Builder(this)
            builder.setTitle(question?.question+"?")
            builder.setItems(question?.answers?.toTypedArray()) {_, which ->
                if(which == question?.correctAnswer){
                    val reward:String = question.reward!!
                    val rewardType:String = question.rewardType!!
                    if(rewardType == BACKPACK){
                        if(backPack?.containsValue(reward)!!){
                            Toast.makeText(this, "You already have a $reward in your backpack", Toast.LENGTH_SHORT).show()
                        } else {
                            backPack?.put(backPack?.size.toString(), reward)
                            backPackRef?.updateChildren(backPack!!)
                            Toast.makeText(this, "Correct, a $reward has been added to your backpack!", Toast.LENGTH_LONG).show()
                        }

                    } else if(rewardType == ADVICE){
                        Toast.makeText(this, "Listen carefully $reward", Toast.LENGTH_LONG).show()
                    }



                } else{
                    Toast.makeText(this, "Wrong answer", Toast.LENGTH_SHORT).show()
                }
            }
            val dialog = builder.create()
            dialog.show()


        })

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