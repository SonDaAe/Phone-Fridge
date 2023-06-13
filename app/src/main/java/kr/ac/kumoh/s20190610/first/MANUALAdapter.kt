package kr.ac.kumoh.s20190610.first

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kr.ac.kumoh.s20190610.first.RecipeAdapter.RecipeViewHolder

data class MANUAL(
    val manual01: String,
    val manualImg01: String,
    val manual02: String,
    val manualImg02: String,
    val manual03: String,
    val manualImg03: String,
    val manual04: String,
    val manualImg04: String,
    val manual05: String,
    val manualImg05: String,
    val manual06: String,
    val manualImg06: String,
    val manual07: String,
    val manualImg07: String,
    val manual08: String,
    val manualImg08: String,
    val manual09: String,
    val manualImg09: String,
    val manual10: String,
    val manualImg10: String,
    val manual11: String,
    val manualImg11: String,
    val manual12: String,
    val manualImg12: String,
    val manual13: String,
    val manualImg13: String,
    val manual14: String,
    val manualImg14: String,
    val manual15: String,
    val manualImg15: String,
    val manual16: String,
    val manualImg16: String,
    val manual17: String,
    val manualImg17: String,
    val manual18: String,
    val manualImg18: String,
    val manual19: String,
    val manualImg19: String,
    val manual20: String,
    val manualImg20: String
)


class MANUALAdapter : RecyclerView.Adapter<MANUALAdapter.MANUALViewHolder>() {

    private val manuals: MutableList<MANUAL> = mutableListOf()

    fun setMANUALs(data: List<MANUAL>) {
        manuals.clear()
        manuals.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MANUALViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipelist, parent, false)
        return MANUALViewHolder(view)
    }

    override fun onBindViewHolder(holder: MANUALViewHolder, position: Int) {
        val manual = manuals[position]
        holder.bind(manual)
    }



    override fun getItemCount(): Int {
        return manuals.size
    }

    inner class MANUALViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView01: ImageView = itemView.findViewById(R.id.imageView_thumb_01)
        private val textViewName01: TextView = itemView.findViewById(R.id.textView_name_01)

        private val imageView02: ImageView = itemView.findViewById(R.id.imageView_thumb_02)
        private val textViewName02: TextView = itemView.findViewById(R.id.textView_name_02)

        private val imageView03: ImageView = itemView.findViewById(R.id.imageView_thumb_03)
        private val textViewName03: TextView = itemView.findViewById(R.id.textView_name_03)

        private val imageView04: ImageView = itemView.findViewById(R.id.imageView_thumb_04)
        private val textViewName04: TextView = itemView.findViewById(R.id.textView_name_04)

        private val imageView05: ImageView = itemView.findViewById(R.id.imageView_thumb_05)
        private val textViewName05: TextView = itemView.findViewById(R.id.textView_name_05)

        private val imageView06: ImageView = itemView.findViewById(R.id.imageView_thumb_06)
        private val textViewName06: TextView = itemView.findViewById(R.id.textView_name_06)

        private val imageView07: ImageView = itemView.findViewById(R.id.imageView_thumb_07)
        private val textViewName07: TextView = itemView.findViewById(R.id.textView_name_07)



        fun bind(manual: MANUAL) {
            textViewName01.text = manual.manual01
            textViewName02.text = manual.manual02
            textViewName03.text = manual.manual03
            textViewName04.text = manual.manual04
            textViewName05.text = manual.manual05
            textViewName06.text = manual.manual06
            textViewName07.text = manual.manual07
            textViewName01.text = manual.manual01
            if (manual.manualImg01 != null && manual.manualImg01.isNotEmpty()) {
                Picasso.get().load(manual.manualImg01).into(imageView01)
            }

            textViewName02.text = manual.manual02
            if (manual.manualImg02 != null && manual.manualImg02.isNotEmpty()) {
                Picasso.get().load(manual.manualImg02).into(imageView02)
            }

            textViewName03.text = manual.manual03
            if (manual.manualImg03 != null && manual.manualImg03.isNotEmpty()) {
                Picasso.get().load(manual.manualImg03).into(imageView03)
            }

            textViewName04.text = manual.manual04
            if (manual.manualImg04 != null && manual.manualImg04.isNotEmpty()) {
                Picasso.get().load(manual.manualImg04).into(imageView04)
            }

            textViewName05.text = manual.manual05
            if (manual.manualImg05 != null && manual.manualImg05.isNotEmpty()) {
                Picasso.get().load(manual.manualImg05).into(imageView05)
            }

            textViewName06.text = manual.manual06
            if (manual.manualImg06 != null && manual.manualImg06.isNotEmpty()) {
                Picasso.get().load(manual.manualImg06).into(imageView06)
            }

            textViewName07.text = manual.manual07
            if (manual.manualImg07 != null && manual.manualImg07.isNotEmpty()) {
                Picasso.get().load(manual.manualImg07).into(imageView07)
            }

        }

    }



}

