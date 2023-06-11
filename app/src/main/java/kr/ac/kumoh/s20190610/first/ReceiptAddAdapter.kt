package kr.ac.kumoh.s20190610.first

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.internal.TextWatcherAdapter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ReceiptAddAdapter (val context: Context, val productList: ArrayList<ProductData>) : BaseAdapter() {
    fun addItem(product: ProductData) {
        productList.add(product)
        this.notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return productList.size
    }

    override fun getItem(position: Int): ProductData {
        return productList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.receipt_add_item_view, null)

        val inputName = view.findViewById<EditText>(R.id.input_name)
        val inputCategory = view.findViewById<EditText>(R.id.input_category)
        val inputQuantity = view.findViewById<EditText>(R.id.input_quantity)
        val inputExp = view.findViewById<EditText>(R.id.input_exp)
        val btn_add_calender = view.findViewById<ImageButton>(R.id.btn_add_item_calender)
        val btn_delete_item = view.findViewById<ImageButton>(R.id.btn_item_delete)

        val data = productList[position]

        inputName.setText(data.productName)
        inputCategory.setText(data.category)
        inputQuantity.setText(data.quantity.toString())
        inputExp.setText(data.exp.toString())

        var calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)


        inputName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //동작 없음
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //동작 없음
            }

            override fun afterTextChanged(p0: Editable?) {
                productList[position].productName = p0.toString()
            }
        })

        inputCategory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //동작 없음
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //동작 없음
            }

            override fun afterTextChanged(p0: Editable?) {
                productList[position].category = p0.toString()
            }

        })

        inputQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //동작 없음
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //동작 없음
            }

            override fun afterTextChanged(p0: Editable?) {
                val quantity = p0.toString()
                if (quantity == "-" || quantity == "") {
                    productList[position].quantity = 0
                }
                else {
                    productList[position].quantity = quantity.toInt()
                }
            }

        })

        inputExp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //동작 없음
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //동작 없음
            }

            override fun afterTextChanged(p0: Editable?) {
                val exp = p0.toString()
                if (exp == "-" || exp == "") {
                    productList[position].exp = 0
                }
                else {
                    productList[position].exp = exp.toInt()
                }
                Log.d("TC_TEST", exp)
            }
        })

        btn_add_calender.setOnClickListener {
            val datePickerDialog = DatePickerDialog(context, { _, year, month, day ->
                val selected = Calendar.getInstance()
                selected.set(year, month, day)

                val today = Calendar.getInstance()

                val differenceInMillis = selected.timeInMillis - today.timeInMillis
                val differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis)

                inputExp.setText(differenceInDays.toString())
            }, year, month, day)

            datePickerDialog.show()
        }

        btn_delete_item.setOnClickListener {
            productList.removeAt(position)
            this.notifyDataSetChanged()
        }



        return view
    }

    fun getData(): ArrayList<ProductData> {
        return productList
    }
}