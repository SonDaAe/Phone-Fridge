package kr.ac.kumoh.s20190610.first

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kr.ac.kumoh.s20190610.first.RecipeAdapter.RecipeViewHolder

data class Recipe(val imageId: Int, val imageUrl: String, val imageName : String)

class RecipeAdapter : RecyclerView.Adapter<RecipeViewHolder>() {

    private val recipes: MutableList<Recipe> = mutableListOf()

    fun setRecipes(data: List<Recipe>) {
        recipes.clear()
        recipes.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageView_thumb)
        private val textViewName: TextView = itemView.findViewById(R.id.textView_name)
//        private val textViewSummary: TextView = itemView.findViewById(R.id.textView_summary)

        fun bind(recipe: Recipe) {
            textViewName.text = recipe.imageName
//            textViewSummary.text = recipe.imageSummary

            if (recipe.imageUrl != null) {
                Picasso.get().load(recipe.imageUrl).into(imageView)
            } else {
                imageView.setImageResource(R.drawable.banana)
            }
        }
    }



}

