package com.example.smartnotes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartnotes.MainActivity
import com.example.smartnotes.databinding.ActivitySignupBinding
import com.example.smartnotes.utils.AuthManager

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSignup.setOnClickListener {
            attemptSignup()
        }

        binding.tvLogin.setOnClickListener {
            finish() // Go back to login
        }
    }

    private fun attemptSignup() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Validate name
        if (name.isEmpty()) {
            binding.etName.error = "Name required"
            return
        }

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

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Please confirm password"
            return
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords don't match"
            return
        }

        // For demo: Create account (in real app: send to backend)
        AuthManager.login(this, email, name)

        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

        // Navigate to main app
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}