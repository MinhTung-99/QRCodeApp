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
import com.qrcodeapp.model.Scan

class ScanAdapter(
    private var scanArr: ArrayList<Scan>,
    private val btnDeleteOnClickListener: BtnDeleteOnClickListener
) : RecyclerView.Adapter<ScanAdapter.ScanViewHolder>() {

    class ScanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val imgTypeQr = itemView.findViewById<ImageView>(R.id.img_type_scan)
        private val txtText = itemView.findViewById<TextView>(R.id.txt_text_qr_code)
        private val txtDate = itemView.findViewById<TextView>(R.id.txt_date)
        private val txtTime = itemView.findViewById<TextView>(R.id.txt_time)
        val btnDelete = itemView.findViewById<Button>(R.id.btn_delete)


        fun addScan(scan: Scan){
            //convert byte to bitmap
            val bitmap = BitmapFactory.decodeByteArray(scan.image, 0, scan.image!!.size)
            imgTypeQr.setImageBitmap(bitmap)
            txtText.text = scan.text
            txtDate.text = scan.date
            txtTime.text = scan.time
        }
    }

    fun setScanArr(scanArr: ArrayList<Scan>){
        this.scanArr = scanArr
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_scan, parent, false)
        return ScanViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
        holder.addScan(scanArr[position])

        holder.btnDelete.setOnClickListener {
            btnDeleteOnClickListener.btnDeleteOnClick(scanArr[position].id!!)
        }
    }

    override fun getItemCount(): Int = scanArr.size

    interface BtnDeleteOnClickListener {
        fun btnDeleteOnClick(id: Int)
    }
}