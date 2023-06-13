package kr.ac.kumoh.s20190610.first

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationBarView

class ListActivity : AppCompatActivity() {
    val homeFragment = HomeFragment()
    val recipeFragment = RecipeFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        //supportActionBar?.setDisplayShowTitleEnabled(false)

        supportFragmentManager.beginTransaction()
            .replace(R.id.contaiers, HomeFragment())
            .commit()
        val navigationBarView = findViewById<NavigationBarView>(R.id.bottom_navigationview)

        navigationBarView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.contaiers, homeFragment)
                        .commit()
                    return@setOnItemSelectedListener true
                }
                R.id.recipe -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.contaiers, recipeFragment)
                        .commit()
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
    }
}