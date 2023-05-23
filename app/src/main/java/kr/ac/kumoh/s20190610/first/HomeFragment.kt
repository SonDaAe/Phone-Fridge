package kr.ac.kumoh.s20190610.first

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.PopupMenu
import android.widget.ListView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var listView1: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 파일을 inflate하여 View 객체를 생성합니다.
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 버튼을 찾아서 클릭 리스너를 등록합니다.
        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener(this)

        listView1 = view.findViewById(R.id.ListView1)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        listView1.adapter = adapter

        //아이템 클릭시 삭제
        listView1.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = adapter.getItem(position)
            adapter.remove(selectedItem)
            adapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 아이디로 버튼 찾아서 클릭 리스너 등록
        val addButton = view.findViewById<Button>(R.id.button)

        /*// Set onClickListener for the button
        addButton.setOnClickListener {
            // Start the addActivity
            val intent = Intent(activity, AddActivity::class.java)
            startActivity(intent)
        }*/
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> showPopup()
        }
    }

    private fun showPopup() {
        // 팝업 메뉴를 띄우는 코드를 작성합니다.
        val popupMenu = PopupMenu(requireActivity(), requireView().findViewById(R.id.button))
        popupMenu.inflate(R.menu.add_popup)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_menu1 -> {
                    startActivity(Intent(requireActivity(), CameraActivity::class.java))
                    true
                }
                R.id.add_menu2 -> {
                    startActivity(Intent(requireActivity(), AddActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}