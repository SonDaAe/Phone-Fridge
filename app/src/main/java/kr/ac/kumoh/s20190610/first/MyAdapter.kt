package kr.ac.kumoh.s20190610.first

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter(val itemList: ArrayList<MyItem>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    private var homeFragment: HomeFragment? = null

    // ViewHolder 클래스
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productText: TextView = itemView.findViewById(R.id.tv_item_name)
        val expirationDateText: TextView = itemView.findViewById(R.id.tv_item_date)
        val numText: TextView = itemView.findViewById(R.id.tv_item_counts)
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