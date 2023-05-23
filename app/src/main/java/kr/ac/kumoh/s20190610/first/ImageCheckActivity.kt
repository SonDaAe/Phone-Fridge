package kr.ac.kumoh.s20190610.first

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import kr.ac.kumoh.s20190610.first.databinding.ActivityImageCheckBinding

class ImageCheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageCheckBinding
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageCheckBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = binding.cameraImage

        val imageData = intent.getByteArrayExtra("imageData")

        displayImage(imageData)
    }

    private fun displayImage(imageData: ByteArray?) {

        if (imageData != null) {
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            imageView.setImageBitmap(bitmap)
        }
    }
}