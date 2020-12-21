package com.qrcodeapp.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.qrcodeapp.R
import com.qrcodeapp.adapter.TypeQrCodeAdapter
import com.qrcodeapp.model.TypeQrCode
import java.util.*

class TypeQrCodeFragment : Fragment(R.layout.fragment_type_qrcode), TypeQrCodeAdapter.RecyclerviewOnClickListener {
    private lateinit var imgBack: ImageView
    private lateinit var adapter: TypeQrCodeAdapter
    private lateinit var typeQrCodes: ArrayList<TypeQrCode>
    private lateinit var rvTypeQrCode: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)

        imgBack.setOnClickListener {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            navController.popBackStack()
        }

        setAdapter()
    }

    private fun initView(view: View) {
        imgBack = view.findViewById(R.id.img_back)
        rvTypeQrCode = view.findViewById(R.id.rv_type_qrcode)
    }

    private fun setAdapter() {
        typeQrCodes = ArrayList<TypeQrCode>()

        val qrPhone = TypeQrCode(R.drawable.ic_phone, "Phone number")
        val qrText = TypeQrCode(R.drawable.ic_text, "Text")
        val qrSMS = TypeQrCode(R.drawable.ic_sms, "SMS")
        val qrURL = TypeQrCode(R.drawable.ic_website, "URL")
        val qrWifi = TypeQrCode(R.drawable.ic_wifi, "Wifi")
        val qrEmail = TypeQrCode(R.drawable.ic_email, "Email")

        typeQrCodes.add(qrPhone)
        typeQrCodes.add(qrText)
        typeQrCodes.add(qrSMS)
        typeQrCodes.add(qrURL)
        typeQrCodes.add(qrWifi)
        typeQrCodes.add(qrEmail)

        adapter = TypeQrCodeAdapter(typeQrCodes, this)
        rvTypeQrCode.adapter = adapter
    }

    override fun recyclerviewOnClick(position: Int) {
        val action = TypeQrCodeFragmentDirections.actionTypeQrCodeFragmentToCreateQrCodeFragment(position)
        findNavController().navigate(action)
    }
}