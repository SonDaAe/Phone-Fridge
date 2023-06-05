package kr.ac.kumoh.s20190610.first

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

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

        val recyclerView = view.findViewById<RecyclerView>(R.id.RecyclerView1)
        val adapter = MyAdapter(itemList)
        recyclerView.adapter = adapter

        // 버튼을 찾아서 클릭 리스너를 등록(+ 버튼)
        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = requireView().findViewById(R.id.RecyclerView1)

        // 사용자 정의 어댑터로 대체
        adapter = MyAdapter(itemList)
        adapter.notifyDataSetChanged()

        adapter.setHomeFragment(this)

        //RecyclerView 초기화, 어댑터 설정
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            //옆으로 밀어 삭제
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.removeItem(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // 알림 채널 생성, 유통기한 확인하고 알림전송
        createNotificationChannel()
        checkExpirationDates()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            // 알림 채널을 시스템에 등록
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //유통기한 체크
    fun checkExpirationDates() {
        val today = Calendar.getInstance() // 현재 날짜와 시간을 가져옴
        today.set(Calendar.HOUR_OF_DAY, 0)

        val notificationItems = ArrayList<MyItem>()

        for (item in itemList) {
            val expirationDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).parse(item.expirationDate)

            //남은 일수 계산
            val remainingDays = (expirationDate.time - today.timeInMillis) / (24 * 60 * 60 * 1000)
            if (remainingDays <= 3L && remainingDays >= 1L) {
                notificationItems.add(item)
            }
            else if (remainingDays < 0L) {
                sendNotification("유통기한이 지난 상품이 있습니다.")
            }
        }

        if (notificationItems.isNotEmpty()) {
            sendNotification("유통기한이 얼마 안 남은 상품이 있습니다.")
        //sendNotification(notificationItems)
        }
    }

    private fun sendNotification(message: String) {
        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.refrigerator)
            .setContentTitle("유통기한 알림")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // 알림 터치했을 때 메인화면으로
        val intent = Intent(requireContext(), ListActivity::class.java)
        //val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        notificationBuilder.setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> showPopup()
        }
    }

    private fun showPopup() {
        // 팝업 메뉴 띄우기
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

    private fun dispatchTakePictureIntent()  { //카메라 앱 실행
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            // TODO: 카메라 캡처 완료 후 처리할 내용 작성
        }

        else if (requestCode == ADD_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val product = data.getStringExtra("product")
            val expirationDate = data.getStringExtra("expirationDate")
            val num = data.getStringExtra("num")

            // 받아온 데이터로 아이템 추가
            if (product != null && expirationDate != null && num != null) {
                val newItem = MyItem(product, expirationDate, num)
                adapter.addItem(newItem)
            }
        }

        else if (requestCode == EDIT_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val updatedCount = data.getIntExtra("updatedCount", 0)
            val updatedPosition = data.getIntExtra("updatedPosition", -1)

            if (updatedPosition != -1) {
                val myItem = itemList[updatedPosition]
                myItem.num = updatedCount.toString()
                adapter.notifyItemChanged(updatedPosition)
            }
        }
    }

    companion object {
        private const val ADD_ACTIVITY_REQUEST_CODE = 100
        const val EDIT_ACTIVITY_REQUEST_CODE = 200

        private const val CHANNEL_ID = "my_channel_id"
        private const val CHANNEL_NAME = "My Channel"
        private const val CHANNEL_DESCRIPTION = "My Channel Description"
        private const val NOTIFICATION_ID = 1
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
