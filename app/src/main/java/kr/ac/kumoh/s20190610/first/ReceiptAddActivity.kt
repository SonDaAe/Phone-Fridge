package kr.ac.kumoh.s20190610.first

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import kr.ac.kumoh.s20190610.first.databinding.ActivityReceiptAddBinding

//class ReceiptAddActivity : AppCompatActivity() {
//    //private lateinit var binding: ActivityReceiptAddBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //binding = ActivityReceiptAddBinding.inflate(layoutInflater)
//        //requestWindowFeature(Window.FEATURE_NO_TITLE)
//        //setContentView(binding.root)
//        setContentView(R.layout.activity_receipt_add)
//
//        val data = intent.getSerializableExtra("data") as ArrayList<ProductData>
//
//
//
//    }
//}

class ReceiptAddActivity(private val context : AppCompatActivity) {
    private lateinit var listener: DialogOKClickedListener
    private lateinit var binding : ActivityReceiptAddBinding
    private val dlg = Dialog(context)
    private val tempData = ArrayList<ProductData>()

    private lateinit var receiptAddListView : ListView
    private lateinit var registerButton : AppCompatButton
    private lateinit var cancelButton: AppCompatButton
    private lateinit var productAddButton: AppCompatButton

    fun show(content: ArrayList<ProductData>) {
        binding = ActivityReceiptAddBinding.inflate(context.layoutInflater)

        receiptAddListView = binding.receiptAddListview
        registerButton = binding.addBtnRegister
        cancelButton = binding.addBtnCancel
        productAddButton = binding.btnProductAdd

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)

        val receiptAddAdapter = ReceiptAddAdapter(context, content)
        receiptAddListView.adapter = receiptAddAdapter

        registerButton.setOnClickListener {

            val length = receiptAddAdapter.count

            for (i in 0 until length) {
                tempData.add(receiptAddAdapter.getItem(i))
            }
            listener.onOKClicked(tempData)
            dlg.dismiss()
        }

        cancelButton.setOnClickListener {
            dlg.dismiss()
        }

        productAddButton.setOnClickListener {
            val addData = ProductData("", 0, 1, 0, "", 7)
            receiptAddAdapter.addItem(addData)
            receiptAddListView.smoothScrollToPosition(receiptAddAdapter.count - 1)
        }
        val window = dlg.window
        val layoutParams = window?.attributes
        val displayMetrics = context.resources.displayMetrics
        val dialogDiffence = (displayMetrics.widthPixels * 0.1).toInt()
//        val dialogWidth = (displayMetrics.widthPixels * 0.85).toInt() // 화면 너비의 85%
//        val dialogHeight = (displayMetrics.heightPixels * 0.85).toInt() // 화면 높이의 85%
        layoutParams?.width = displayMetrics.widthPixels - dialogDiffence
        layoutParams?.height = displayMetrics.heightPixels - dialogDiffence
        window?.attributes = layoutParams

        val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.dialog_rounded_bg)

        dlg.window!!.apply {
            setBackgroundDrawable(backgroundDrawable)
            //setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        }

        dlg.setCanceledOnTouchOutside(true)
        dlg.setCancelable(false)

        dlg.show()
    }

    fun setOnOKClickedListener(listener: (ArrayList<ProductData>) -> Unit) {
        this.listener = object : DialogOKClickedListener {
            override fun onOKClicked(content: ArrayList<ProductData>) {
                listener(content)
            }
        }
    }

    interface DialogOKClickedListener {
        fun onOKClicked(content : ArrayList<ProductData>)
    }
}