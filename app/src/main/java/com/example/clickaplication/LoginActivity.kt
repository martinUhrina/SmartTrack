package com.example.clickaplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var icon: ImageView

    // Staticky zadané prihlasovacie údaje pre demonštráciu, treba zmeniť
    private val correctUsername = "admin"
    private val correctPassword = "admin"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.name)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.connect)
        icon = findViewById(R.id.icon)

        loginButton.setOnClickListener {
            if (login()){
                val intent = Intent(this, showData::class.java)
                startActivity(intent)
            }
        }
        icon.setOnClickListener{
            Toast.makeText(this, "Vitajte v aplikácii SmartTrack! Poskytujeme vám spoľahlivé sledovanie vašej aktivity a zdravia.", Toast.LENGTH_LONG).show()
        }
    }

    private fun login() : Boolean {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Overenie, či sú vstupné údaje správne
        if (username == correctUsername && password == correctPassword) {
            Toast.makeText(this, "Prihlásenie úspešné.", Toast.LENGTH_SHORT).show()
            return true;
            // Tu by mohla byť logika pre prechod do inej aktivity po úspešnom prihlásení
        }
        else if (username.isEmpty()){
            Toast.makeText(this, "Meno nesmie byť prázde.", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()){
            Toast.makeText(this, "Heslo nesmie byť prázdne.", Toast.LENGTH_SHORT).show()
        }
        else if (username.isEmpty() && password.isEmpty()){
            Toast.makeText(this, "Zadajte prihlasovacie údaje.", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "Nesprávne meno alebo heslo.", Toast.LENGTH_SHORT).show()
        }
        return false;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsMenu::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}