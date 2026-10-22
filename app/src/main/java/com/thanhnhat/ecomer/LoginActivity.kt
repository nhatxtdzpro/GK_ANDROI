package com.thanhnhat.ecomer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.thanhnhat.ecomer.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) { openHome(); return }
        binding = ActivityLoginBinding.inflate(layoutInflater); setContentView(binding.root)
        binding.loginButton.setOnClickListener { signIn() }
        binding.registerButton.setOnClickListener { register() }
    }
    private fun credentials(): Pair<String, String>? {
        val email = binding.emailEdit.text.toString().trim(); val password = binding.passwordEdit.text.toString()
        if (email.isBlank() || password.length < 6) { toast("Nhập email và mật khẩu ít nhất 6 ký tự"); return null }
        return email to password
    }
    private fun signIn() { credentials()?.let { (email, password) -> loading(true); auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { loading(false); if (it.isSuccessful) openHome() else toast(it.exception?.localizedMessage ?: "Đăng nhập thất bại") } } }
    private fun register() { credentials()?.let { (email, password) -> loading(true); auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { loading(false); if (it.isSuccessful) openHome() else toast(it.exception?.localizedMessage ?: "Không thể tạo tài khoản") } } }
    private fun loading(show: Boolean) { binding.progress.visibility = if (show) View.VISIBLE else View.GONE; binding.loginButton.isEnabled = !show; binding.registerButton.isEnabled = !show }
    private fun openHome() { startActivity(Intent(this, MainActivity::class.java)); finish() }
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
