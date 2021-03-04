package com.marcdelacruz.triviahouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

private const val RC_SIGN_IN = 9001
private const val TAG = "SignInActivity"
class SigninActivity : AppCompatActivity() {
    private var signInButton: SignInButton? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var firebaseAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)


        signInButton = findViewById<SignInButton>(R.id.sign_in_button)

        signInButton?.setOnClickListener(signInClick)

        var gso: GoogleSignInOptions = GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()





    }

    private val signInClick = View.OnClickListener { _ ->
        val signInIntent: Intent? = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                var account = task.result
                firebaseAuthWithGoogle(account)
            } catch(e: ApiException){
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?){
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct?.id)
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)
            if(!task.isSuccessful){
                Log.w(TAG, "signInWithCredential", task.exception)
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            } else{
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }


}