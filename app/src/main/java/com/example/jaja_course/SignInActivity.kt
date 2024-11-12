package com.example.jaja_course

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Инициализируем Firebase Authentication
        auth = FirebaseAuth.getInstance()

        val loginButton: Button = findViewById(R.id.login_button)
        val emailEditText: EditText = findViewById(R.id.username_edit_text)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Успешная авторизация", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Закрыть текущую activity, чтобы нельзя было вернуться к экрану входа
                        } else {
                            // Ошибка авторизации
                            Toast.makeText(this, "Ошибка авторизации: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
