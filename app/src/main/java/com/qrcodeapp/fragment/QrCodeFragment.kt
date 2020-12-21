package com.qrcodeapp.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.qrcodeapp.R
import com.qrcodeapp.adapter.QRCodeAdapter
import com.qrcodeapp.db.QRCodeDB
import com.qrcodeapp.model.QRCode
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class QrCodeFragment : Fragment(R.layout.fragment_qrcode), QRCodeAdapter.BtnQRCodeListener {

    private val REQUEST_WRITE_EXTERNAL_STORAGE = 1002

    private lateinit var btnCreate: Button
    private lateinit var ll_Empty: LinearLayout
    private lateinit var btnToolbarCreate: Button

    private lateinit var adapter: QRCodeAdapter
    private lateinit var rvQrCode: RecyclerView
    private lateinit var qrCodes: ArrayList<QRCode>
    private lateinit var db: QRCodeDB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)

        checkPermission()

        btnCreate.setOnClickListener {
            findNavController().navigate(R.id.typeQrCodeFragment)
        }

        btnToolbarCreate.setOnClickListener {
            findNavController().navigate(R.id.typeQrCodeFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        qrCodes = db.getAllQrCodes()
        setAdapter()
        checkVisibility()
    }

    private fun setAdapter(){
        adapter = QRCodeAdapter(db.getAllQrCodes(), this)
        rvQrCode.adapter = adapter
    }

    private fun initView(view: View) {
        db = QRCodeDB(requireContext())
        qrCodes = ArrayList()
        btnCreate = view.findViewById(R.id.btn_create)
        rvQrCode = view.findViewById(R.id.rv_qr_code)
        ll_Empty = view.findViewById(R.id.ll_empty)
        btnToolbarCreate = view.findViewById(R.id.btn_toolbar_create)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE
            )
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { }
            else {
                Toast.makeText(context, "ban can phai cho phep de doc ghi", Toast.LENGTH_SHORT).show()
                checkPermission()
            }
        }
    }

    private fun checkVisibility() {
        if (qrCodes.size > 0) {
            ll_Empty.visibility = View.GONE
            btnCreate.visibility = View.VISIBLE
        } else {
            ll_Empty.visibility = View.VISIBLE
            btnToolbarCreate.visibility = View.GONE
        }
    }

    override fun btnDeleteQRCode(id: Int) {
        db.deleteQrCode(id)
        onStart()
    }

    override fun btnShareQRCode(image: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

        val path: String = saveBitmapToFile(bitmap).path
        val file = File(path)
        val intent = Intent(Intent.ACTION_SEND)
        val myUri = FileProvider.getUriForFile(
                requireContext(),
                context?.packageName + ".fileprovider",
                file
            )

        intent.setDataAndType(myUri,"image/*")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(Intent.EXTRA_STREAM, myUri)
        intent.putExtra(Intent.EXTRA_TEXT, "I am sharing a image QRCode")
        activity?.startActivity(intent)
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/saved_images_QRCode")
        myDir.mkdirs()
        val fName = "ImageQR.jpg"
        val file = File(myDir, fName)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }
}