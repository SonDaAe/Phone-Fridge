package kr.ac.kumoh.s20190610.first

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kr.ac.kumoh.s20190610.first.databinding.ActivityReceiptAddBinding

class ReceiptAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReceiptAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getSerializableExtra("data") as ArrayList<ProductData>



    }
}