package com.example.libroapi

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private var option = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //declaraciones para nav bar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        searchEditText = findViewById(R.id.search_edittext)
        searchButton = findViewById(R.id.search_button)

        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        //Refresher
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            actualizarDatos()
        }
        //logica para Mostrar lista de datos

        Enqueue(option)

        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        searchButton.setOnClickListener {
            val searchText = searchEditText.text.toString()
            if (searchText.isNotEmpty()) {
                Enqueue(option, searchText)
                actualizarDatos(searchText)
            }
        }

    }

    //opciones de la nav
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.home_button_nav -> actualizarDatos()
            R.id.Insert_button_nav -> {
                val intent = Intent(this@MainActivity, InsertActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.sortn_button_nav -> {
                option = 1
                actualizarDatos()
            }
            R.id.sorti_button_nav -> {
                option = 2
                actualizarDatos()
            }
            R.id.sort_button_nav -> {
                option = 0
                actualizarDatos()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun actualizarDatos(searchText: String? = null) {
        Enqueue(option, searchText)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun Enqueue(option: Int, searchText: String? = null){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val call: Call<MutableList<PostModel>>

        val regex = "^\\d{13}\$".toRegex()
        val isISBN = searchText?.matches(regex) ?: false

        if (isISBN) {
            val isbn = searchText!!
                call = serviceGenerator.searchPostsId(isbn)
        }else {
            if (searchText != null && searchText.isNotEmpty()) {
                call = serviceGenerator.searchPosts(searchText)
            } else {
                when (option) {
                    1 -> call = serviceGenerator.getPostsSortn()
                    2 -> call = serviceGenerator.getPostsSorti()
                    else -> call = serviceGenerator.getPosts()
                }
            }
        }
        val intent = Intent(this, SelectActivity::class.java)

        call.enqueue(object : Callback<MutableList<PostModel>> {
            override fun onResponse(call: Call<MutableList<PostModel>>, response: Response<MutableList<PostModel>>) {
                if(response.isSuccessful){
                    recyclerView.apply {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        val adapter = PostAdapter(response.body()!!)
                        this.adapter = adapter
                        adapter.setOnItemClickListener(object : PostAdapter.OnItemClickListener {
                            override fun onItemClick(postModel: PostModel) {
                                intent.putExtra("bookname", postModel.bookname)
                                intent.putExtra("author", postModel.author)
                                intent.putExtra("isbn", postModel.isbn)
                                intent.putExtra("price", postModel.price)
                                intent.putExtra("avil", postModel.avil)
                                intent.putExtra("quantity", postModel.quantity)
                                intent.putExtra("year", postModel.year)
                                intent.putExtra("date", postModel.date)
                                startActivity(intent)
                                finish()
                            }
                        })
                    }
                }
            }

            override fun onFailure(call: Call<MutableList<PostModel>>, t: Throwable) {
                t.printStackTrace()
                Log.e("error", t.message.toString())
            }
        })
    }

    //funciones necesarias para que la nav no crashee
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
}