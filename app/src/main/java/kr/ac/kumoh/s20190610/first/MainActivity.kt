package kr.ac.kumoh.s20190610.first

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rotaanimation = AnimationUtils.loadAnimation(this, R.anim.rotation)

        val imageView1 = findViewById<ImageView>(R.id.image_view1)
        val imageView2 = findViewById<ImageView>(R.id.image_view2)
        val imageView3 = findViewById<ImageView>(R.id.image_view3)
        val imageView4 = findViewById<ImageView>(R.id.image_view4)
        val imageView5 = findViewById<ImageView>(R.id.image_view5)
        val imageView6 = findViewById<ImageView>(R.id.image_view6)
        val imageView7 = findViewById<ImageView>(R.id.image_view7)
        val imageView8 = findViewById<ImageView>(R.id.image_view8)
        val imageView9 = findViewById<ImageView>(R.id.image_view9)
        val imageView10 = findViewById<ImageView>(R.id.image_view10)
        val imageView11 = findViewById<ImageView>(R.id.image_view11)
        val imageView12 = findViewById<ImageView>(R.id.image_view12)

        imageView1.startAnimation(rotaanimation)
        imageView2.startAnimation(rotaanimation)
        imageView3.startAnimation(rotaanimation)
        imageView4.startAnimation(rotaanimation)
        imageView5.startAnimation(rotaanimation)
        imageView6.startAnimation(rotaanimation)
        imageView7.startAnimation(rotaanimation)
        imageView8.startAnimation(rotaanimation)
        imageView9.startAnimation(rotaanimation)
        imageView10.startAnimation(rotaanimation)
        imageView11.startAnimation(rotaanimation)
        imageView12.startAnimation(rotaanimation)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ListActivity::class.java))
            finish()
        }, 3000)
    }
}