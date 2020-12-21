package com.qrcodeapp.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.qrcodeapp.R
import com.qrcodeapp.db.QRCodeDB
import com.qrcodeapp.model.QRCode
import java.io.ByteArrayOutputStream

class CreateQrCodeFragment : Fragment(R.layout.fragment_create_qrcode) {

    private lateinit var imgBackCreateQrCode: ImageView
    private lateinit var txtQrCode: TextView
    private lateinit var imgOk: ImageView
    private lateinit var imgTypeQrCode: ImageView
    private lateinit var txtTypeQrCode: TextView
    private lateinit var edtTypeQrCode: EditText
    private lateinit var txtMessage: TextView
    private lateinit var edtMessage: EditText

    private val args: CreateQrCodeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)

        setupUI()

        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        imgOk.setOnClickListener {
            createQrCode(edtTypeQrCode.text.toString())

            navController.popBackStack()
            navController.popBackStack()

            //hide keyboard
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        imgBackCreateQrCode.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun initView(view: View) {
        imgBackCreateQrCode = view.findViewById(R.id.img_back_create_qrcode)
        txtQrCode = view.findViewById(R.id.txt_qr_code)
        imgOk = view.findViewById(R.id.img_ok)
        imgTypeQrCode = view.findViewById(R.id.img_type_qr_code)
        txtTypeQrCode = view.findViewById(R.id.txt_type_qr_code)
        edtTypeQrCode = view.findViewById(R.id.edt_type_qr_code)
        txtMessage = view.findViewById(R.id.txt_message)
        edtMessage = view.findViewById(R.id.edt_message)
    }

    private fun setupUI() {
        when (args.position) {
            0 -> {
                edtTypeQrCode.inputType = InputType.TYPE_CLASS_NUMBER
                setData(R.drawable.ic_phone, "Phone number", "Phone number")
                setVisibility(false)
            }
            1 -> {
                setData(R.drawable.ic_text, "Text", "Text")
                setVisibility(false)
            }
            2 -> {
                edtTypeQrCode.inputType = InputType.TYPE_CLASS_NUMBER
                setData(R.drawable.ic_sms, "SMS", "To")
                txtMessage.text = "Message :"
                setVisibility(true)
            }
            3 -> {
                setData(R.drawable.ic_website, "URL", "URL")
                setVisibility(false)
            }
            4 -> {
                setData(R.drawable.ic_wifi, "Wifi", "Name")
                txtMessage.text = "Password:"
                setVisibility(true)
            }
            5 -> {
                setData(R.drawable.ic_email, "Email", "Email")
                setVisibility(false)
            }
        }
    }

    private fun setVisibility(isCheck: Boolean) {
        if (isCheck) {
            txtMessage.visibility = View.VISIBLE
            edtMessage.visibility = View.VISIBLE
        } else {
            txtMessage.visibility = View.GONE
            edtMessage.visibility = View.GONE
        }
    }

    private fun setData(imageTypeQrCode: Int, textQrCode: String, textTypeQrCode: String) {
        imgTypeQrCode.setImageResource(imageTypeQrCode)
        txtQrCode.text = textQrCode
        txtTypeQrCode.text = "$textTypeQrCode :"
    }

    private fun addQrCode(byteArray: ByteArray) {
        val db = QRCodeDB(requireContext())
        val qrCode: QRCode
        when (args.position) {
            0 -> {
                qrCode = QRCode(edtTypeQrCode.text.toString(), "phone", byteArray)
                db.addQrCode(qrCode)
            }
            1 -> {
                qrCode = QRCode(edtTypeQrCode.text.toString(), "text", byteArray)
                db.addQrCode(qrCode)
            }
            2 -> {
                val strSMS = """
                To: ${edtTypeQrCode.text}
                Message: ${edtMessage.text}
                """.trimIndent()
                qrCode = QRCode(strSMS, "sms", byteArray)
                db.addQrCode(qrCode)
            }
            3 -> {
                qrCode = QRCode(edtTypeQrCode.text.toString(), "url", byteArray)
                db.addQrCode(qrCode)
            }
            4 -> {
                val strWifi = """
                S: ${edtTypeQrCode.text}
                P: ${edtMessage.text}
                """.trimIndent()
                qrCode = QRCode(strWifi, "wifi", byteArray)
                db.addQrCode(qrCode)
            }
            5 -> {
                qrCode = QRCode(edtTypeQrCode.text.toString(), "email", byteArray)
                db.addQrCode(qrCode)
            }
        }
    }

    private fun createQrCode(text: String) {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200)
            val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565)
            for (x in 0..199) {
                for (y in 0..199) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            addQrCode(byteArray)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}