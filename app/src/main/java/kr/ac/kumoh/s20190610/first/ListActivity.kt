package kr.ac.kumoh.s20190610.first

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)


        val btn_add = findViewById<Button>(R.id.button)
        btn_add.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }
}