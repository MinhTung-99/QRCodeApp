package com.qrcodeapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.qrcodeapp.R

class SettingFragment : Fragment(R.layout.fragment_setting) {

    private lateinit var switchSound: SwitchCompat
    private lateinit var switchVibrate: SwitchCompat
    private lateinit var switchHistory: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)

        getSetting()

        switchSound.isChecked = isSound
        switchVibrate.isChecked = isVibrate
        switchHistory.isChecked = isHistory

        switchSound.setOnCheckedChangeListener { _, b ->
            isSound = b
            saveSetting()
        }

        switchVibrate.setOnCheckedChangeListener { _, b ->
            isVibrate = b
            saveSetting()
        }

        switchHistory.setOnCheckedChangeListener { _, b ->
            isHistory = b
            saveSetting()
        }
    }

    private fun initView(view: View) {
        switchSound = view.findViewById(R.id.switch_sound)
        switchVibrate = view.findViewById(R.id.switch_vibrate)
        switchHistory = view.findViewById(R.id.switch_history)
    }

    private fun saveSetting() {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(sound, isSound)
        editor.putBoolean(vibrate, isVibrate)
        editor.putBoolean(history, isHistory)
        editor.commit()
    }

    fun getSetting() {
        sharedPreferences = context?.getSharedPreferences(myPreferences, Context.MODE_PRIVATE)!!
        if (sharedPreferences.contains(sound)) {
            isSound = sharedPreferences.getBoolean(sound, true)
        }
        if (sharedPreferences.contains(vibrate)) {
            isVibrate = sharedPreferences.getBoolean(vibrate, true)
        }
        if (sharedPreferences.contains(history)) {
            isHistory = sharedPreferences.getBoolean(history, true)
        }
    }

    companion object{
        var isSound = true
        var isVibrate = true
        var isHistory = true

        private val myPreferences = "setting"
        private val sound = "soundKey"
        private val vibrate = "vibrateKey"
        private val history = "historyKey"
    }
}