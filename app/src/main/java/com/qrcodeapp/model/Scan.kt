package com.qrcodeapp.model

class Scan(text: String? = null, date: String? = null, time: String? = null, image: ByteArray? = null) {
    var id: Int? = null
    var text: String? = text
    var date: String? = date
    var time: String? = time
    var image: ByteArray? = image
}