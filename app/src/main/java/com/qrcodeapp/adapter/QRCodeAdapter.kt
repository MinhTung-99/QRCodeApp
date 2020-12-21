package com.qrcodeapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qrcodeapp.R
import com.qrcodeapp.model.QRCode

class QRCodeAdapter(
    private val qrCodes: ArrayList<QRCode>,
    private val btnQRCodeListener: BtnQRCodeListener
) : RecyclerView.Adapter<QRCodeAdapter.QRCodeViewHolder>() {

    class QRCodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgQRCode = itemView.findViewById<ImageView>(R.id.img_qrcode)
        val txtText = itemView.findViewById<TextView>(R.id.txt_text_qr_code)
        val txtTypeQRCode = itemView.findViewById<TextView>(R.id.type_qrcode_txt)
        val btnDeleteQRCode = itemView.findViewById<Button>(R.id.btn_delete_qr_code)
        val btnShareQRCode = itemView.findViewById<Button>(R.id.btn_share_qr_code)

        fun addData(qrCode: QRCode){
            //convert byte to bitmap
            val bitmap =
                BitmapFactory.decodeByteArray(qrCode.image, 0, qrCode.image.size)

            imgQRCode.setImageBitmap(bitmap)
            txtText.text = qrCode.text
            txtTypeQRCode.text = qrCode.typeQR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRCodeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_qrcode, parent, false)
        return QRCodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: QRCodeViewHolder, position: Int) {
        holder.addData(qrCodes[position])

        holder.btnDeleteQRCode.setOnClickListener {
            btnQRCodeListener.btnDeleteQRCode(qrCodes[position].id)
        }

        holder.btnShareQRCode.setOnClickListener {
            btnQRCodeListener.btnShareQRCode(qrCodes[position].image)
        }
    }

    override fun getItemCount(): Int = qrCodes.size

    interface BtnQRCodeListener{
        fun btnDeleteQRCode(id: Int)
        fun btnShareQRCode(image: ByteArray)
    }
}