package kr.ac.kumoh.s20190610.first

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        var option = findViewById<ImageButton>(R.id.pic_btn)

        option.setOnClickListener {
            var popupMenu = PopupMenu(applicationContext, it)

            menuInflater?.inflate(R.menu.popup, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_menu1 -> {
                        Toast.makeText(applicationContext, "첫번째1클릭", Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        Toast.makeText(applicationContext, "두번째2클릭",Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }
}