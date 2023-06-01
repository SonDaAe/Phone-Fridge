package kr.ac.kumoh.s20190610.first

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

    val REQUEST_IMAGE_CAPTURE_PERMISSION = 1
    val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var adapter: MyAdapter
    private lateinit var recyclerView: RecyclerView
    private val itemList: ArrayList<MyItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 레이아웃 파일을 inflate하여 View 객체를 생성
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 버튼을 찾아서 클릭 리스너를 등록(+ 버튼)
        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener(this)

        //아이템 클릭시 삭제
        /*listView1.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = adapter.getItem(position)
            adapter.remove(selectedItem)
            adapter.notifyDataSetChanged()
        }*/

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = requireView().findViewById(R.id.RecyclerView1)

        // 사용자 정의 어댑터로 대체
        adapter = MyAdapter(itemList)
        adapter.notifyDataSetChanged()

        //RecyclerView 초기화, 어댑터 설정
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> showPopup()
        }
    }

    private fun showPopup() {
        // 팝업 메뉴를 띄움
        val popupMenu = PopupMenu(requireActivity(), requireView().findViewById(R.id.button))
        popupMenu.inflate(R.menu.add_popup)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add_menu1 -> {
                    showCameraGalleryOptions()
                    true
                }
                R.id.add_menu2 -> {
                    startActivityForResult(Intent(requireActivity(), AddActivity::class.java), ADD_ACTIVITY_REQUEST_CODE)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showCameraGalleryOptions() {
        val popupMenu = PopupMenu(requireActivity(), requireView().findViewById(R.id.button))
        popupMenu.inflate(R.menu.popup)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_menu1-> {
                    // 갤러리
                    val gallery = Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media")) //갤러리 연결
                    startActivity(gallery)
                    return@setOnMenuItemClickListener true
                }
                R.id.action_menu2 -> {
                    // 카메라
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val cameraPermission = Manifest.permission.CAMERA
                        val permissionGranted = ContextCompat.checkSelfPermission(requireActivity(), cameraPermission) == PackageManager.PERMISSION_GRANTED
                        if (permissionGranted) {
                            dispatchTakePictureIntent()
                        } else {
                            requestPermissions(arrayOf(cameraPermission), REQUEST_IMAGE_CAPTURE_PERMISSION)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun dispatchTakePictureIntent() { //카메라 앱 실행
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    //권한 요청 결과 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_CAPTURE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            //TODO
        }

        if (requestCode == ADD_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val product = data?.getStringExtra("product")
            val expirationDate = data?.getStringExtra("expirationDate")
            val num = data?.getStringExtra("num")

            // 받아온 데이터로 아이템 추가
            if (product != null && expirationDate != null && num != null) {
                val newItem = MyItem(product, expirationDate, num)
                adapter.addItem(newItem)
            }
        }
    }

    companion object {
        private const val ADD_ACTIVITY_REQUEST_CODE = 100
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
