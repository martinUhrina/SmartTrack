package com.example.clickaplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class SettingsMenu : AppCompatActivity() {

    private lateinit var setButton: Button
    private lateinit var backButton: Button
    private lateinit var weight: EditText
    private lateinit var step_distance: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_menu)

        weight = findViewById(R.id.vaha)
        step_distance = findViewById(R.id.krok)
        setButton = findViewById(R.id.setButton)
        backButton = findViewById(R.id.backButton)

        setButton.setOnClickListener {
            Toast.makeText(this, "Hodnoty boli ulozene.", Toast.LENGTH_SHORT).show()
            val sharedPreferences = getSharedPreferences("Constant", MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putString("weight", weight.text.toString())
            editor.putString("step_distance", step_distance.text.toString())

            editor.apply()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}