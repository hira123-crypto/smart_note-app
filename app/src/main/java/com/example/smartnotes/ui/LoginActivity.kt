package com.example.smartnotes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartnotes.MainActivity
import com.example.smartnotes.databinding.ActivityLoginBinding
import com.example.smartnotes.utils.AuthManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        binding.tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validate email
        if (email.isEmpty()) {
            binding.etEmail.error = "Email required"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Invalid email format"
            return
        }

        // Validate password
        if (password.isEmpty()) {
            binding.etPassword.error = "Password required"
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return
        }

        // For demo: Any valid email/password works
        // In real app: Check against backend/database
        AuthManager.login(this, email, email.substringBefore("@"))

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

        // Navigate to main app
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}