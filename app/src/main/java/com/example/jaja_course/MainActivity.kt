package com.example.jaja_course

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Проверка авторизации пользователя
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser == null) {
            // Если пользователь не авторизован, перенаправить на экран регистрации
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Метод, вызываемый при нажатии на аватарку
    fun onAvatarClick(view: View) {
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            // Если пользователь авторизован, открываем профиль
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        } else {
            // Если пользователь не авторизован, перенаправляем на экран регистрации
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    // Метод для перехода к курсу ООП
    fun openCourseOOP(view: View) {
        val intent = Intent(this, CourseOOPActivity::class.java)
        startActivity(intent)
    }

    // Метод для перехода к курсу по функциям
    fun openCourseFunctions(view: View) {
        val intent = Intent(this, CourseFunctionsActivity::class.java)
        startActivity(intent)
    }

    // Метод для перехода к курсу по циклам
    fun openCourseLoops(view: View) {
        val intent = Intent(this, CourseLoopsActivity::class.java)
        startActivity(intent)
    }

    // Метод для перехода к курсу по переменным и типам данных
    fun openCourseVariables(view: View) {
        val intent = Intent(this, ActivityCourseVariables::class.java)
        startActivity(intent)
    }

}
