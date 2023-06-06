package kr.ac.kumoh.s20190610.first

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var photo: ImageButton
    private lateinit var registerBtn: Button
    private lateinit var product: EditText
    private lateinit var buyDate: TextView
    private lateinit var expirationDate: TextView
    private lateinit var storage: Spinner
    private lateinit var num: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        checkPermission()

        photo = findViewById(R.id.pic_btn)
        product = findViewById(R.id.product)
        expirationDate = findViewById(R.id.expiration_date)
        buyDate = findViewById(R.id.buy_date)
        storage = findViewById(R.id.spinner)
        num = findViewById(R.id.num)

        registerBtn = findViewById(R.id.register_btn)
        registerBtn.setOnClickListener {
            val product = product.text.toString()
            val expirationDate = expirationDate.text.toString()
            val num = num.text.toString()
            val buyDate = buyDate.text.toString()
            val storage = storage.selectedItem.toString()

            if (product.isEmpty() || expirationDate.isEmpty() || num.isEmpty()) {
                Toast.makeText(this, "상품명, 수량, 유통기한을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent()
            intent.putExtra("product", product)
            intent.putExtra("expirationDate", expirationDate)
            intent.putExtra("num", num)
            intent.putExtra("storage", storage)
            intent.putExtra("buyDate", buyDate)
            setResult(Activity.RESULT_OK, intent)

            // AddActivity 종료
            finish()
        }

        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        var option = findViewById<ImageButton>(R.id.pic_btn)
        var cancel = findViewById<Button>(R.id.cancel_btn)

        var cal_btn = findViewById<ImageButton>(R.id.calendar)
        var cal_btn2 = findViewById<ImageButton>(R.id.calendar2)
        var buy_date = findViewById<TextView>(R.id.buy_date)
        var expi_date = findViewById<TextView>(R.id.expiration_date)

        cal_btn.setOnClickListener { //구매일자 표시
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)

                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)

                buy_date.text = formattedDate
            }, year, month, day)
            datePickerDialog.show()
        }

        cal_btn2.setOnClickListener { //유통기한 표시
            val datePickerDialog = DatePickerDialog(this, { _, year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)

                val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)

                expi_date.text = formattedDate
            }, year, month, day)
            datePickerDialog.show()
        }

        option.setOnClickListener { //카메라 버튼 눌렀을 때
            var popupMenu = PopupMenu(applicationContext, it)

            menuInflater?.inflate(R.menu.popup, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_menu1 -> {
                        Toast.makeText(applicationContext, "갤러리", Toast.LENGTH_SHORT).show()
                        val gallery = Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media")) //갤러리 연결
                        startActivity(gallery)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.action_menu2 -> {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePictureIntent ->
                                takePictureIntent.resolveActivity(packageManager)?.also {
                                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                                }
                            }
                        Toast.makeText(applicationContext, "카메라",Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener true
                    }
                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }
        }

        cancel.setOnClickListener {
            finish()
        }

    }

    private fun checkPermission() {
        var permission = mutableMapOf<String, String>()
        permission["camera"] = Manifest.permission.CAMERA
        //permission["storageRead"] = Manifest.permission.READ_EXTERNAL_STORAGE
        //permission["storageWrite"] = Manifest.permission.WRITE_EXTERNAL_STORAGE

        var denied = permission.count { ContextCompat.checkSelfPermission(this, it.value) == PackageManager.PERMISSION_DENIED }

        if(denied > 0 && Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            requestPermissions(permission.values.toTypedArray(), REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            var count = grantResults.count { it == PackageManager.PERMISSION_DENIED }

            if(count != 0) {
                Toast.makeText(applicationContext, "권한을 동의해주세요.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            val pic_btn = findViewById<ImageButton>(R.id.pic_btn)
            pic_btn.setImageBitmap(imageBitmap)
        }
    }
}
