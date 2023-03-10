package com.example.libroapi

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class InsertActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.insert)

        val dateTextView: EditText = findViewById(R.id.booksViewerDate)
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        val ButtonInsert: Button = findViewById(R.id.insertButt)

        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        ButtonInsert.setOnClickListener{
            insertButtonLogic()
        }

        dateTextView.setOnClickListener {
            DateLogic(dateTextView)
        }

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.home_button_nav ->{
                    val intent = Intent(this@InsertActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            drawer.closeDrawer(GravityCompat.START)
            return true

    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navMenu: Menu = navView.menu
        val nav1: MenuItem = navMenu.findItem(R.id.Insert_button_nav)
        val nav2: MenuItem = navMenu.findItem(R.id.SortGroup)


        nav1.isVisible = false
        nav1.isEnabled = false
        nav2.isVisible = false
        nav2.isEnabled = false
    }
    private fun DateLogic(dateTextView: TextView){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth  ->
                val selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
                dateTextView.text = selectedDate
            },
            year,
            month,
            day
        )
        // Show the DatePickerDialog
        datePickerDialog.show()
    }

    private fun insertButtonLogic(){
        var avil = false
        val booknameEditText: EditText = findViewById(R.id.booksViewerBookname)
        val bookname = booknameEditText.text.toString()
        val authorEditText: EditText = findViewById(R.id.booksViewerAuthor)
        val author = authorEditText.text.toString()
        val yearEditText: EditText = findViewById(R.id.booksViewerYear)
        val year = yearEditText.text.toString().toInt()
        val priceEditText: EditText = findViewById(R.id.booksViewerPrice)
        val price = priceEditText.text.toString().toInt()
        val quantityEditText: EditText = findViewById(R.id.booksViewerQuantity)
        val quantity = quantityEditText.text.toString().toInt()
        val avilRadioGroup = findViewById<RadioGroup>(R.id.booksViewerAvil)
        val avilRadioButton = findViewById<RadioButton>(avilRadioGroup.checkedRadioButtonId)
        val avils = avilRadioButton.text.toString()
        if (avils == "Available") {
            avil = true
        }
        val isbnEditText: EditText = findViewById(R.id.booksViewerISBN)
        val isbn = isbnEditText.text.toString().toLong()

        val dateEditText: EditText = findViewById(R.id.booksViewerDate)
        val dateStr = dateEditText.text.toString()


        val book = PostModel(bookname, author, year, price, quantity, avil, dateStr, isbn)

        val apiService = ServiceGenerator.buildService(ApiService::class.java)
        apiService.createPost(book).enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                Log.d("onResponse", "Response received. Status code: ${response.code()}")

                if(response.isSuccessful){
                    val intent = Intent(this@InsertActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val errorBody = response.errorBody()?.string()
                    Log.e("Error", "Request failed with code ${response.code()}. Error message: $errorBody")
                }
            }

            override fun onFailure(call: Call<PostModel>, t: Throwable) {
                Log.e("onFailure", "Request failed: ${t.message}")
                t.printStackTrace()
                val response = (t as? HttpException)?.response()
                response?.errorBody()?.string()?.let { Log.e("Error Body", it)}
                }
        })
    }


}