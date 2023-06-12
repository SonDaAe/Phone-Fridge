package kr.ac.kumoh.s20190610.first


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = RecipeAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Volley 라이브러리를 사용하여 서버에서 데이터 요청
        val requestQueue = Volley.newRequestQueue(requireContext())
        val url = "https://recipeexpress.run.goorm.site/Image" // 이미지 데이터를 전송하는 엔드포인트 URL로 변경해야 합니다.

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONArray> { response ->
                try {
                    // JSON 데이터를 처리하여 원하는 작업 수행
                    val data = mutableListOf<Recipe>()

                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val imageId = jsonObject.optInt("recipe_id") // getInt() 대신 optInt() 사용
                        val imageUrl = jsonObject.optString("ATT_FILE_NO_MAIN") // getString() 대신 optString() 사용
                        val imageName = jsonObject.optString("RCP_NM") // getString() 대신 optString() 사용

                        // 이미지 데이터를 사용하여 필요한 작업 수행

                        // Recipe 객체 생성 및 데이터 설정
                        val recipe = Recipe(imageId, imageUrl, imageName)
                        data.add(recipe)
                    }

                    // 어댑터에 데이터 설정
                    adapter.setRecipes(data)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )

        // 요청을 큐에 추가하여 서버로부터 데이터를 가져옵니다.
        requestQueue.add(jsonArrayRequest)
    }
}
