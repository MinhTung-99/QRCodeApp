package com.qrcodeapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qrcodeapp.R
import com.qrcodeapp.model.TypeQrCode

class TypeQrCodeAdapter(
        private val typeQrCodes: ArrayList<TypeQrCode>,
        private val recyclerviewOnClickListener: RecyclerviewOnClickListener
) : RecyclerView.Adapter<TypeQrCodeAdapter.TypeQrCodeViewHolder>() {

    class TypeQrCodeViewHolder(
            itemView: View,
            private val recyclerviewOnClickListener: RecyclerviewOnClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val imgTypeQrCode = itemView.findViewById<ImageView>(R.id.img_type_qrcode)
        private val txtTextQrCode = itemView.findViewById<TextView>(R.id.txt_text_qrcode)

        init {
            itemView.setOnClickListener(this)
        }

        fun addData(typeQrCode: TypeQrCode){
            imgTypeQrCode.setImageResource(typeQrCode.image)
            txtTextQrCode.text = typeQrCode.text
        }

        override fun onClick(v: View?) {
            recyclerviewOnClickListener.recyclerviewOnClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeQrCodeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_type_qrcode, parent, false)
        return TypeQrCodeViewHolder(view, recyclerviewOnClickListener)
    }

    override fun onBindViewHolder(holder: TypeQrCodeViewHolder, position: Int) {
        holder.addData(typeQrCodes[position])
    }

    override fun getItemCount(): Int = typeQrCodes.size

    interface RecyclerviewOnClickListener {
        fun recyclerviewOnClick(position: Int)
    }
}