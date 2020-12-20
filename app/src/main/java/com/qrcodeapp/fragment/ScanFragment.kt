package com.qrcodeapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.qrcodeapp.R
import com.qrcodeapp.db.ScanDB
import com.qrcodeapp.model.Scan
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ScanFragment : Fragment(R.layout.fragment_scan) {

    private val REQUEST_CAMERA = 1001
    private var cameraPreview: SurfaceView? = null
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    private var isBarcodeDetector = true

    private var barcode: SparseArray<Barcode>? = null

    private lateinit var imgCamera: ImageView
    private var isCameraBack = false

    //flash
    private lateinit var imgFlash: ImageView
    private var cameraManager: CameraManager? = null
    private var isFlash = false

    private var cameraID: Int = CameraSource.CAMERA_FACING_BACK

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)

        checkPermission()

        createCameraBarcodeDetector()

        imgCamera.setOnClickListener {
            cameraSource!!.release()
            isCameraBack = if (!isCameraBack) {
                setFacingCameraBarcodeDetector(CameraSource.CAMERA_FACING_FRONT)
                true
            } else {
                setFacingCameraBarcodeDetector(CameraSource.CAMERA_FACING_BACK)
                false
            }
        }

        imgFlash.setOnClickListener {
            val camaraId = cameraManager!!.cameraIdList[0]
            cameraManager?.setTorchMode(camaraId, false)
        }
    }

    private fun initView(view: View) {
        cameraPreview = view.findViewById(R.id.surfaceView)
        cameraManager = context?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        imgCamera = view.findViewById(R.id.img_camera)
        imgFlash = view.findViewById(R.id.img_flash)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
            return
        }
    }

    private fun createCameraBarcodeDetector() {
        barcodeDetector =
            BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build()
        cameraSource = CameraSource.Builder(context, barcodeDetector)
            .setFacing(cameraID)
            .setRequestedFps(35.0f)
            .setAutoFocusEnabled(true)
            .build()
        cameraPreview!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    cameraSource?.start(cameraPreview!!.holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {}
            override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
                cameraSource?.stop()
                cameraSource?.release()
                barcodeDetector?.release()
            }
        })
        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}
            override fun receiveDetections(detections: Detections<Barcode>?) {
                activity?.runOnUiThread {
                    barcode = detections?.detectedItems
                    if (barcode != null && barcode?.size()!! > 0) {
                        if (isBarcodeDetector) {
//                            if (SettingFragment.isHistory) {
                            handleTypeQRCode(barcode?.valueAt(0)!!.valueFormat)
//                            }
//                            if (SettingFragment.isSound) {
//                                val mediaPlayer: MediaPlayer =
//                                    MediaPlayer.create(context, R.raw.beep)
//                                mediaPlayer.start()
//                            }
//                            if (SettingFragment.isVibrate) {
//                                val v =
//                                    context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//                                v.vibrate(400)
//                            }
//
                            //Dialog
                            val builder = AlertDialog.Builder(
                                activity
                            )
                            builder.setTitle("Result: ")
                            builder.setMessage(barcode?.valueAt(0)!!.rawValue)
                            builder.setPositiveButton(
                                "OK"
                            ) { dialogInterface, i -> isBarcodeDetector = true }
                            builder.setCancelable(false) //chỉ click OK mới đóng dialog
                            val alertDialog = builder.create()
                            alertDialog.show()
                        }
                        isBarcodeDetector = false
                    }
                }
            }
        })
    }

    private fun handleTypeQRCode(valueFormat: Int) {
        when (valueFormat) {
            Barcode.TEXT -> addDB(R.drawable.ic_text)
            Barcode.PHONE -> addDB(R.drawable.ic_phone)
            Barcode.SMS -> addDB(R.drawable.ic_sms)
            Barcode.URL -> addDB(R.drawable.ic_website)
            Barcode.WIFI -> addDB(R.drawable.ic_wifi)
            Barcode.EMAIL -> addDB(R.drawable.ic_email)
            else -> addDB(R.drawable.ic_empty)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setFacingCameraBarcodeDetector(CameraSource.CAMERA_FACING_BACK)
            } else {
                Toast.makeText(context, "ban can phai cho phep de scan", Toast.LENGTH_SHORT).show()
                checkPermission()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setFacingCameraBarcodeDetector(cameraFacing: Int) {
        cameraID = cameraFacing
        cameraSource = CameraSource.Builder(context, barcodeDetector)
            .setFacing(cameraID)
            .setRequestedFps(35.0f)
            .setAutoFocusEnabled(true)
            .build()
        try {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            cameraSource?.start(cameraPreview?.holder)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}
            override fun receiveDetections(detections: Detections<Barcode>?) {
                activity?.runOnUiThread {
                    barcode = detections?.detectedItems
                    if (barcode != null && barcode?.size()!! > 0) {
                        if (isBarcodeDetector) {
//                            if (SettingFragment.isHistory) {
                            handleTypeQRCode(barcode?.valueAt(0)!!.valueFormat)
//                            }
//                            if (SettingFragment.isSound) {
//                                val mediaPlayer: MediaPlayer =
//                                    MediaPlayer.create(context, R.raw.beep)
//                                mediaPlayer.start()
//                            }
//                            if (SettingFragment.isVibrate) {
//                                val v =
//                                    context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//                                v.vibrate(400)
//                            }

                            //Dialog
                            val builder = AlertDialog.Builder(
                                activity
                            )
                            builder.setTitle("Result:")
                            builder.setMessage(barcode?.valueAt(0)!!.rawValue)
                            builder.setPositiveButton(
                                "OK"
                            ) { dialogInterface, i -> isBarcodeDetector = true }
                            builder.setCancelable(false) //chỉ click OK mới đóng dialog
                            val alertDialog = builder.create()
                            alertDialog.show()
                        }
                        isBarcodeDetector = false
                    }
                }
            }
        })
    }

    private fun convertImageToByteArray(myImage: Int): ByteArray? {
        val image = BitmapFactory.decodeResource(
            resources,
            myImage
        )

        // convert bitmap to byte
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }
    private fun addDB(myImage: Int) {
        val image = convertImageToByteArray(myImage)
        val qrCodeSan = Scan(barcode!!.valueAt(0).rawValue, getDateNow(), getTimeNow(), image)
        val db = ScanDB(requireContext())
        db.addQrCodeScanDB(qrCodeSan)
    }

    private fun getDateNow(): String? {
        val dfDate: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dfDate.format(Date())
    }

    private fun getTimeNow(): String? {
        val dfTime: DateFormat = SimpleDateFormat("hh:mm")
        return dfTime.format(Date())
    }

}