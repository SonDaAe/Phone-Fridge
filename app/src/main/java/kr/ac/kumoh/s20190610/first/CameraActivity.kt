package kr.ac.kumoh.s20190610.first

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.app.ActivityCompat
import kr.ac.kumoh.s20190610.first.databinding.ActivityCameraBinding
import java.io.File

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    private lateinit var imageReader: ImageReader

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var surfaceView: SurfaceView

    private val cameraId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val display = this.applicationContext?.resources?.displayMetrics
        val screenWidth: Int = display?.widthPixels ?: 0
        val previewHeight: Int = screenWidth * 1

        initImageReader()


        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("Camera_test", screenWidth.toString())
        Log.d("Camera_test", previewHeight.toString())
        surfaceView = binding.surfaceView

//        val layoutParams_sv = surfaceView.layoutParams
//        layoutParams_sv.height = 1800
//        layoutParams_sv.width = 1800
//        surfaceView.layoutParams = layoutParams_sv

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager

        surfaceView.holder.setFixedSize(screenWidth, previewHeight)
        Log.d("Camera_test", surfaceView.width.toString())
        Log.d("Camera_test", surfaceView.height.toString())
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                openCamera()
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                Log.d("Camera_test", "surface_changed")
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                cameraDevice.close()
            }
        })

        binding.btnShot.setOnClickListener {
            takePicture()
        }

    }

    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 0)
                return
            }

            cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    Log.d("Camera_test", "open")
                    cameraDevice = camera
                    createCameraPreviewSession()
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Log.d("Camera_test", "close")
                    cameraDevice.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.d("Camera_test", "close")
                    cameraDevice.close()
                }
            }, null)
        } catch (e: CameraAccessException) {
            Log.d("Camera_test", "Error")
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val surface = surfaceView.holder.surface

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    startPreview()
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.d("Camera_test", "onConfigured")
                }
            }, null)
        } catch (e: CameraAccessException) {
            TODO("")
        }
    }
    private fun startPreview() {
        try {
            val captureRequest = captureRequestBuilder.build()

            cameraCaptureSession.setRepeatingRequest(captureRequest, null, null)
        } catch (e: CameraAccessException) {
            TODO("")
        }
    }
    private fun takePicture() {
        val captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureBuilder.addTarget(imageReader.surface)

        cameraCaptureSession.capture(captureBuilder.build(), object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                val image = imageReader.acquireLatestImage()
                val buffer = image.planes[0].buffer
                val imageData = ByteArray(buffer.remaining())
                buffer.get(imageData)
                image.close()

                val intent = Intent(this@CameraActivity, ImageCheckActivity::class.java)
                intent.putExtra("imageData", imageData)
                startActivity(intent)
            }
        }, null)
    }

    private fun initImageReader() {
        val imageDimension = getPreviewSize() // 이미지의 너비와 높이를 가져오는 함수 (이전에 정의된 부분)
        print(imageDimension)
        // 이미지 리더를 생성합니다.
        imageReader = ImageReader.newInstance(
            imageDimension.width, // 이미지의 너비
            imageDimension.height, // 이미지의 높이
            ImageFormat.JPEG, // 이미지 포맷 (JPEG로 설정)
            1 // 동시에 처리할 최대 이미지 수
        )

        // 이미지가 준비되었을 때 호출되는 콜백을 설정합니다.
        imageReader.setOnImageAvailableListener(
            { reader ->
                // 이미지를 가져옵니다.
                val image = reader?.acquireLatestImage()
                // 이미지 처리 및 다음 단계로 전달하는 로직을 구현합니다.
                // ...
                // 필요한 경우 이미지를 메모리에서 해제합니다.
                image?.close()
            },
            null // Handler를 사용하지 않으므로 null로 설정
        )
    }

    private fun getPreviewSize(): Size {
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = manager.cameraIdList[0] // 카메라 ID (원하는 카메라 선택)
        val characteristics = manager.getCameraCharacteristics(cameraId)
        val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val outputSizes = map?.getOutputSizes(SurfaceTexture::class.java)

        // 원하는 가로:세로 비율을 선택합니다 (여기서는 16:9 비율)
        val desiredRatio = 16.0 / 9.0
        var bestSize: Size? = null
        var minDiff = Double.MAX_VALUE

        outputSizes?.let { sizes ->
            for (size in sizes) {
                val ratio = size.width.toDouble() / size.height.toDouble()
                val diff = Math.abs(ratio - desiredRatio)
                if (diff < minDiff) {
                    bestSize = size
                    minDiff = diff
                }
            }
        }

        // 선택된 가장 적합한 사이즈를 반환합니다.
        return bestSize ?: outputSizes?.get(0) ?: Size(1920, 1080) // 기본값 설정
    }
}