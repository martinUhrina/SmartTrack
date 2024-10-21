package com.example.clickaplication

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
//import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clickaplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var listView: ListView
    private lateinit var zoznamNajdenych: List<String> // Predpokladá sa, že zoznamNajdenych je zoznam názvov prvkov


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        swipeRefreshLayout = findViewById(R.id.SwipeRefresh)
        listView = findViewById(android.R.id.list)


        var fakeData = generateFakeData()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fakeData)
        listView.adapter = adapter

        swipeRefreshLayout.setColorSchemeColors(123456)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true

            fakeData = generateFakeData()
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Polozky boli aktualizovane", Toast.LENGTH_LONG).show()
            swipeRefreshLayout.isRefreshing = false
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "Pustam dalsu aktivitu", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ConnectActivity::class.java)
            startActivity(intent)
        }

    }

    private fun generateFakeData(): List<String> {      //pre Testovanie
        val fakeData = mutableListOf<String>()
        for (i in 1..20) {
            fakeData.add("Položka $i") // Generovanie názvov položiek v tvare "Položka 1", "Položka 2", atď.
        }
        return fakeData
    }

}