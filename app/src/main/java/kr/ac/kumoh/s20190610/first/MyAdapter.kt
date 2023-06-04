package kr.ac.kumoh.s20190610.first

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kumoh.s20190610.first.HomeFragment.Companion.EDIT_ACTIVITY_REQUEST_CODE
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter(val itemList: ArrayList<MyItem>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    companion object {
        const val EDIT_ACTIVITY_REQUEST_CODE = 200
    }
    private var homeFragment: HomeFragment? = null

    // ViewHolder 클래스
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val productText: TextView = itemView.findViewById(R.id.tv_item_name)
        val expirationDateText: TextView = itemView.findViewById(R.id.tv_item_date)
        val numText: TextView = itemView.findViewById(R.id.tv_item_counts)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedMyItem = itemList[position]

                    val intent = Intent(itemView.context, EditActivity::class.java).apply {
                        putExtra("position", position)
                        putExtra("product", selectedMyItem.product)
                        putExtra("expirationDate", selectedMyItem.expirationDate)
                        putExtra("num", selectedMyItem.num)
                        putExtra("updatedCount", selectedMyItem.updatedCount)
                    }
                    //itemView.context.startActivity(intent)
                    homeFragment?.startActivityForResult(intent, EDIT_ACTIVITY_REQUEST_CODE)
                }
            }
        }

        fun updateCount(count: String) {
            numText.text = count
            Log.v("updateCount", "호출")
        }
    }

    // onCreateViewHolder: 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder: 뷰홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }
}