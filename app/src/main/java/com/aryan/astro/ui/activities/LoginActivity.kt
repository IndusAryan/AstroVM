package com.aryan.astro.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.aryan.astro.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private lateinit var auth: FirebaseAuth
    val TAG = "LoginAct"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth

        binding?.createAccountButton?.setOnClickListener {
            createAccount(binding?.fieldEmail?.text.toString(), binding?.fieldPassword?.text.toString())
        }

        binding?.signInButton?.setOnClickListener {
            signIn(binding?.fieldEmail?.text.toString(), binding?.fieldPassword?.text.toString())
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHomeFragment()
        }
    }

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        showProgressBar()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideProgressBar()
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    navigateToHomeFragment()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        showProgressBar()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                hideProgressBar()
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    navigateToHomeFragment()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = binding?.fieldEmail?.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding?.fieldEmail?.error = "Required."
            valid = false
        } else {
            binding?.fieldEmail?.error = null
        }

        val password = binding?.fieldPassword?.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding?.fieldPassword?.error = "Required."
            valid = false
        } else {
            binding?.fieldPassword?.error = null
        }

        return valid
    }

    private fun showProgressBar() {
        binding?.progressBar?.isVisible = true
    }

    private fun hideProgressBar() {
        binding?.progressBar?.isGone = true
    }

    private fun navigateToHomeFragment() {
        // Navigate to MainActivity which will load the HomeFragment
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
