package kr.ac.kumoh.s20190610.first

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class RecipeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = RecipeAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val requestQueue = Volley.newRequestQueue(requireContext())
        val url = "https://recipeexpress.run.goorm.site/Image"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    val data = mutableListOf<Recipe>()

                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val imageId = jsonObject.optInt("recipe_id")
                        val imageUrl = jsonObject.optString("ATT_FILE_NO_MAIN")
                        val imageName = jsonObject.optString("RCP_NM")

                        val recipe = Recipe(imageId, imageUrl, imageName)
                        data.add(recipe)
                    }

                    adapter.setRecipes(data)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )

        recyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(requireContext(), recyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val selectedRecipe = adapter.getItem(position)

                        // 이미지 클릭 이벤트 발생 시 MANUALFragment 실행
                        val manualFragment = MANUALFragment.newInstance(position)  // 위치값 전달
                        val fragmentManager = requireActivity().supportFragmentManager
                        fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, manualFragment)
                            .addToBackStack(null)
                            .commit()
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                        // Handle long click event if needed
                    }
                })
        )


        requestQueue.add(jsonArrayRequest)
    }
}

class RecyclerItemClickListener(
    context: Context,
    recyclerView: RecyclerView,
    private val listener: OnItemClickListener
) : RecyclerView.OnItemTouchListener {

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val childView = recyclerView.findChildViewUnder(e.x, e.y)
                if (childView != null) {
                    listener.onLongItemClick(childView, recyclerView.getChildAdapterPosition(childView))
                }
            }
        })
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onLongItemClick(view: View, position: Int)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView = rv.findChildViewUnder(e.x, e.y)
        if (childView != null && gestureDetector.onTouchEvent(e)) {
            listener.onItemClick(childView, rv.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        // No implementation needed
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // No implementation needed
    }
}
