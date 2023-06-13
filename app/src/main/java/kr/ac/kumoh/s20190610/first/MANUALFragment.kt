package kr.ac.kumoh.s20190610.first


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class MANUALFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MANUALAdapter

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): MANUALFragment {
            val fragment = MANUALFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ARG_POSITION, -1) ?: -1
        if (position != -1) {

        }
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = MANUALAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Volley 라이브러리를 사용하여 서버에서 데이터 요청
        val requestQueue = Volley.newRequestQueue(requireContext())
        val url = "https://recipeexpress.run.goorm.site/MANUAL" // 이미지 데이터를 전송하는 엔드포인트 URL로 변경해야 합니다.

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONArray> { response ->
                try {
                    // JSON 데이터를 처리하여 원하는 작업 수행
                    val data = mutableListOf<MANUAL>()
                    Log.d("POSITION", "Position: $position")

                        val jsonObject = response.getJSONObject(position)

                        val manual01 = jsonObject.optString("MANUAL01")
                        Log.d("manual01", "manual01: $jsonObject")
                        val manualImg01 = jsonObject.optString("MANUAL_IMG01")
                        val manual02 = jsonObject.optString("MANUAL02")
                        val manualImg02 = jsonObject.optString("MANUAL_IMG02")
                        val manual03 = jsonObject.optString("MANUAL03")
                        val manualImg03 = jsonObject.optString("MANUAL_IMG03")
                        val manual04 = jsonObject.optString("MANUAL04")
                        val manualImg04 = jsonObject.optString("MANUAL_IMG04")
                        val manual05 = jsonObject.optString("MANUAL05")
                        val manualImg05 = jsonObject.optString("MANUAL_IMG05")
                        val manual06 = jsonObject.optString("MANUAL06")
                        val manualImg06 = jsonObject.optString("MANUAL_IMG06")
                        val manual07 = jsonObject.optString("MANUAL07")
                        val manualImg07 = jsonObject.optString("MANUAL_IMG07")
                        val manual08 = jsonObject.optString("MANUAL08")
                        val manualImg08 = jsonObject.optString("MANUAL_IMG08")
                        val manual09 = jsonObject.optString("MANUAL09")
                        val manualImg09 = jsonObject.optString("MANUAL_IMG09")
                        val manual10 = jsonObject.optString("MANUAL10")
                        val manualImg10 = jsonObject.optString("MANUAL_IMG10")
                        val manual11 = jsonObject.optString("MANUAL11")
                        val manualImg11 = jsonObject.optString("MANUAL_IMG11")
                        val manual12 = jsonObject.optString("MANUAL12")
                        val manualImg12 = jsonObject.optString("MANUAL_IMG12")
                        val manual13 = jsonObject.optString("MANUAL13")
                        val manualImg13 = jsonObject.optString("MANUAL_IMG13")
                        val manual14 = jsonObject.optString("MANUAL14")
                        val manualImg14 = jsonObject.optString("MANUAL_IMG14")
                        val manual15 = jsonObject.optString("MANUAL15")
                        val manualImg15 = jsonObject.optString("MANUAL_IMG15")
                        val manual16 = jsonObject.optString("MANUAL16")
                        val manualImg16 = jsonObject.optString("MANUAL_IMG16")
                        val manual17 = jsonObject.optString("MANUAL17")
                        val manualImg17 = jsonObject.optString("MANUAL_IMG17")
                        val manual18 = jsonObject.optString("MANUAL18")
                        val manualImg18 = jsonObject.optString("MANUAL_IMG18")
                        val manual19 = jsonObject.optString("MANUAL19")
                        val manualImg19 = jsonObject.optString("MANUAL_IMG19")
                        val manual20 = jsonObject.optString("MANUAL20")
                        val manualImg20 = jsonObject.optString("MANUAL_IMG20")

                        val manual = MANUAL(manual01, manualImg01, manual02, manualImg02, manual03, manualImg03, manual04, manualImg04, manual05, manualImg05,
                            manual06, manualImg06, manual07, manualImg07, manual08, manualImg08, manual09, manualImg09, manual10, manualImg10,
                            manual11, manualImg11, manual12, manualImg12, manual13, manualImg13, manual14, manualImg14, manual15, manualImg15,
                            manual16, manualImg16, manual17, manualImg17, manual18, manualImg18, manual19, manualImg19, manual20, manualImg20)

                        data.add(manual)


                    // 어댑터에 데이터 설정
                    adapter.setMANUALs(data)
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
