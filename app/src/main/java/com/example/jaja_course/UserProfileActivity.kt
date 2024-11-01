package com.example.jaja_course

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var profileImageView: ImageView
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
        storageReference = FirebaseStorage.getInstance().reference.child("users/${user.uid}/profile.jpg")

        profileImageView = findViewById(R.id.profile_image_view)
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

        // Обработка нажатия на аватарку для изменения изображения
        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
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
        // Загрузка изображения профиля
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            profileImageView.setImageURI(uri)
        }.addOnFailureListener {
            Toast.makeText(this, "Не удалось загрузить изображение профиля", Toast.LENGTH_SHORT).show()
        }

        // Загрузка имени пользователя из Firestore
        firestore.collection("users").document(user.uid).get().addOnSuccessListener { document ->
            if (document != null && document.contains("name")) {
                val userName = document.getString("name")
                findViewById<EditText>(R.id.name_edit_text).setText(userName)
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
                    findViewById<EditText>(R.id.name_edit_text).setText(newName)
                } else {
                    Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    private fun saveProfileChanges() {
        val newName = findViewById<EditText>(R.id.name_edit_text).text.toString()

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

        // Сохранение изображения профиля в Storage
        val imageUri = profileImageView.tag as? Uri
        if (imageUri != null) {
            storageReference.putFile(imageUri).addOnSuccessListener {
                Toast.makeText(this, "Изображение профиля сохранено", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Ошибка сохранения изображения профиля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                profileImageView.setImageURI(imageUri)
                profileImageView.tag = imageUri
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1001
    }
}
