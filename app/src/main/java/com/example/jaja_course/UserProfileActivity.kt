package com.example.jaja_course

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nameTextView: TextView
    private lateinit var editNameButton: Button
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        // Инициализация Firebase
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser ?: return
        firestore = FirebaseFirestore.getInstance()

        nameTextView = findViewById(R.id.name_text_view)
        editNameButton = findViewById(R.id.edit_name_button)
        saveButton = findViewById(R.id.save_button)
        logoutButton = findViewById(R.id.logoutButton)

        // Обработка нажатия на кнопку изменения имени
        editNameButton.setOnClickListener {
            showEditNameDialog()
        }

        // Обработка нажатия на кнопку сохранения изменений профиля
        saveButton.setOnClickListener {
            saveProfileChanges()
        }

        // Обработка нажатия на кнопку выхода из аккаунта
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        // Загрузка имени пользователя из Firestore
        firestore.collection("users").document(user.uid).get().addOnSuccessListener { document ->
            if (document != null && document.contains("name")) {
                val userName = document.getString("name")
                nameTextView.text = userName
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Не удалось загрузить данные профиля", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditNameDialog() {
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Изменить имя пользователя")
            .setView(editText)
            .setPositiveButton("Сохранить") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotEmpty()) {
                    nameTextView.text = newName
                } else {
                    Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    private fun saveProfileChanges() {
        val newName = nameTextView.text.toString()

        if (newName.isNotEmpty()) {
            // Сохранение имени в Firestore
            val userMap = mapOf("name" to newName)
            firestore.collection("users").document(user.uid).set(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Данные профиля успешно сохранены", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Ошибка сохранения данных профиля", Toast.LENGTH_SHORT).show()
                }
        }
    }
}