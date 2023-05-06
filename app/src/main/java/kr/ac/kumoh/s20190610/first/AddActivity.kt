package kr.ac.kumoh.s20190610.first

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        checkPermission()

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

        option.setOnClickListener {
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