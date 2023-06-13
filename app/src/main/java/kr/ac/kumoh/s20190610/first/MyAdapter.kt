package kr.ac.kumoh.s20190610.first

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kumoh.s20190610.first.HomeFragment.Companion.EDIT_ACTIVITY_REQUEST_CODE
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter(val itemList: ArrayList<MyItem>, private val listener: OnClickListener) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    companion object {
        const val EDIT_ACTIVITY_REQUEST_CODE = 200
    }
    private var homeFragment: HomeFragment? = null

    interface OnClickListener {
        fun minusButtonClickListener(item: MyItem, pos: Int)
        fun plusButtonClickListener(item: MyItem, pos: Int)

        fun thumbnailOnClickListenr(item: MyItem, pos: Int)
        fun deatilOnClickListener(item: MyItem, pos: Int)
    }

    // ViewHolder 클래스
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //val type: TextView = itemView.findViewById(R.id.tv_item_type)
        val productText: TextView = itemView.findViewById(R.id.tv_item_name)
        val expirationDateText: TextView = itemView.findViewById(R.id.tv_item_date)
        val numText: TextView = itemView.findViewById(R.id.tv_item_counts)
        private val minusButton: ImageButton = itemView.findViewById(R.id.btn_minus)
        private val plusButton: ImageButton = itemView.findViewById(R.id.btn_plus)
        private val detailLayout: LinearLayout = itemView.findViewById(R.id.detail_area_layout)
        private val thumbnailView: ImageView = itemView.findViewById(R.id.thumbnail)

        init {
            minusButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedItem = itemList[position]

                    if (selectedItem.num.toInt() > 1) {
                        listener.minusButtonClickListener(selectedItem, position)
                    }
                }
            }

            plusButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedItem = itemList[position]
                    listener.plusButtonClickListener(selectedItem, position)
                }
            }

            detailLayout.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedItem = itemList[position]
                    listener.deatilOnClickListener(selectedItem, position)
                }
            }

            thumbnailView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedItem = itemList[position]
                    listener.thumbnailOnClickListenr(selectedItem, position)
                }
            }
        }

        fun updateCount(count: String) {
            numText.text = count
        }
    }

    // onCreateViewHolder: 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder: 뷰홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.type.text = itemList[position].type
        holder.productText.text = itemList[position].product
        holder.expirationDateText.text = itemList[position].expirationDate
        holder.numText.text = itemList[position].num
    }

    // getItemCount: 아이템 수 반환
    override fun getItemCount(): Int {
        return itemList.count()
    }

    // addItem: 아이템 추가
    fun addItem(item: MyItem) {
        itemList.add(item)
        notifyItemInserted(itemList.size - 1)

        homeFragment?.checkExpirationDates()
    }

    fun setHomeFragment(homeFragment: HomeFragment) {
        this.homeFragment = homeFragment
    }

    fun removeItem(position: Int) : Long {
        val id = itemList[position].id
        itemList.removeAt(position)
        notifyItemRemoved(position)

        return id
    }
}