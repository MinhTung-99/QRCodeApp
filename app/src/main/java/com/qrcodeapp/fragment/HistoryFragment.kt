package com.qrcodeapp.fragment

import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.qrcodeapp.R
import com.qrcodeapp.adapter.ScanAdapter
import com.qrcodeapp.db.ScanDB

class HistoryFragment : Fragment(R.layout.fragment_history), ScanAdapter.BtnDeleteOnClickListener {

    private lateinit var rvHistory: RecyclerView
    private lateinit var adapter: ScanAdapter
    private lateinit var btnClear: Button
    private lateinit var rl_history: RelativeLayout
    private lateinit var db: ScanDB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        if(db.getAllQrCodesScan().size != 0){
            rl_history.visibility = View.GONE
            adapter = ScanAdapter(db.getAllQrCodesScan(),this)
            rvHistory.adapter = adapter
        }else{
            rl_history.visibility = View.VISIBLE
        }

        btnClear.setOnClickListener {
            rl_history.visibility = View.VISIBLE
            db.deleteAllScan()
            adapter.setScanArr(db.getAllQrCodesScan())
        }
    }

    private fun init(view: View){
        db = ScanDB(requireContext())
        rvHistory = view.findViewById(R.id.rv_history)
        btnClear = view.findViewById(R.id.btn_clear)
        rl_history = view.findViewById(R.id.rl_history)
    }

    override fun btnDeleteOnClick(id: Int) {
        db.deleteScan(id)
        adapter.setScanArr(db.getAllQrCodesScan())
        if(db.getAllQrCodesScan().size == 0){
            rl_history.visibility = View.VISIBLE
        }
    }
}