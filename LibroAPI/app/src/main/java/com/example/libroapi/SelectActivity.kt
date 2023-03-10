package com.example.libroapi

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SelectActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select)

        val deletebutton: Button = findViewById(R.id.Deletebutt)
        val updatebutton: Button = findViewById(R.id.updatebutt)

        val dateTextView: EditText = findViewById(R.id.booksViewerDate)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)




        if (intent != null) {
            val isbn: TextView = findViewById(R.id.booksViewerISBN)
            val bookname: EditText = findViewById(R.id.booksViewerBookname)
            val author: EditText = findViewById(R.id.booksViewerAuthor)
            val year: EditText = findViewById(R.id.booksViewerYear)
            val price: EditText = findViewById(R.id.booksViewerPrice)
            val quantity: EditText = findViewById(R.id.booksViewerQuantity)
           // val availability: EditText = findViewById(R.id.booksViewerAvil)
            val radioButtontrue = findViewById<RadioButton>(R.id.radio_available)
            val radioButtonfalse = findViewById<RadioButton>(R.id.radio_not_available)


            isbn.setText(intent.getLongExtra("isbn",0).toString());
            bookname.setText(intent.getStringExtra("bookname"))
            author.setText(intent.getStringExtra("author"))

            val dateString = intent.getStringExtra("date")
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault()).parse(dateString)
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
            dateTextView.setText(formattedDate)

            year.setText(intent.getIntExtra("year",0).toString())
            price.setText(intent.getIntExtra("price",0).toString())
            quantity.setText(intent.getIntExtra("quantity",0).toString())

            if (intent.getBooleanExtra("avil", false)) {
                radioButtontrue.isChecked = true
            }else {radioButtonfalse.isChecked = true}


            Log.e("prueba",intent.getBooleanExtra("avil",false).toString())
        }

        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)





        deletebutton.setOnClickListener{
            deleteBook()
        }

        updatebutton.setOnClickListener{
            updateBook()
        }
        dateTextView.setOnClickListener {

            DateLogic(dateTextView)
        }
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home_button_nav ->{
                val intent = Intent(this@SelectActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.Insert_button_nav ->{
                val intent = Intent(this@SelectActivity, InsertActivity::class.java)
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
        val nav2: MenuItem = navMenu.findItem(R.id.SortGroup)

        nav2.isVisible = false
        nav2.isEnabled = false
    }

    private fun deleteBook(){

        val isbn = intent.getLongExtra("isbn",0)

        val apiService = ServiceGenerator.buildService(ApiService::class.java)
        apiService.deletePost(isbn).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    val intent = Intent(this@SelectActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    val errorBody = response.errorBody()?.string()
                    Log.e("Error", "Request failed with code ${response.code()}. Error message: $errorBody")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("onFailure", "Request failed: ${t.message}")
                t.printStackTrace()
                val response = (t as? HttpException)?.response()
                response?.errorBody()?.string()?.let { Log.e("Error Body", it)}
            }
        })
    }

    private fun updateBook(){
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
        val isbnEditText: TextView = findViewById(R.id.booksViewerISBN)
        val isbn = isbnEditText.text.toString().toLong()

        val dateEditText: EditText = findViewById(R.id.booksViewerDate)
        val dateStr = dateEditText.text.toString()


        val book = PostModel(bookname, author, year, price, quantity, avil, dateStr, isbn)

        val apiService = ServiceGenerator.buildService(ApiService::class.java)
        val call = apiService.updatePost(book)
        call.enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                if(response.isSuccessful){
                    val intent = Intent(this@SelectActivity, MainActivity::class.java)
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