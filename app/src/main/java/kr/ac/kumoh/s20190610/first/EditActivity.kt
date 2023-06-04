package kr.ac.kumoh.s20190610.first

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class EditActivity : AppCompatActivity() {
    private var count: Int = 0
    private var currentPosition: Int = -1

    private lateinit var count_text: TextView
    private lateinit var edit: Button
    private lateinit var cancel: Button
    private lateinit var up: Button
    private lateinit var down: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        edit = findViewById(R.id.edit_btn)
        cancel = findViewById(R.id.cancel_btn)
        up = findViewById(R.id.up_btn)
        down = findViewById(R.id.down_btn)
        count_text = findViewById(R.id.count)

        // 아이템 num 값 받아오기
        val num = intent.getStringExtra("num")
        count = num?.toInt() ?: 0
        val updatedCount = intent.getIntExtra("updatedCount", 0)

        // count 값 초기화
        updateCount()

        // up 클릭 count 증가
        up.setOnClickListener {
            count++
            updateCount()
        }
        // down 클릭 count 감소
        down.setOnClickListener {
            if (count > 0) {
                count--
                updateCount()
            }
        }

        currentPosition = intent.getIntExtra("position", -1)

        edit.setOnClickListener {
            val updatedCountText = count_text.text.toString()
            if (updatedCountText.isNotEmpty()) {
                val updatedCount = updatedCountText.toInt()

                val intent = Intent()
                intent.putExtra("updatedCount", updatedCount)
                intent.putExtra("updatedPosition", currentPosition)
                setResult(Activity.RESULT_OK, intent)

                finish()
            }
        }

        // 수정된 count 값이 있을 경우, 해당 값으로 count 초기화
        if (updatedCount != 0) {
            count = updatedCount
        }

        // count 값 초기화
        updateCount()

        //취소 버튼 눌렀을 때
        cancel.setOnClickListener {
            finish()
        }
    }

    private fun updateCount() {
        count_text.text = count.toString()
    }
}