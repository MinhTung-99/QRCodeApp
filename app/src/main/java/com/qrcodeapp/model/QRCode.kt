package com.qrcodeapp.model

class QRCode(text: String, typeQR: String, image: ByteArray) {
    var id = 0
    var text: String = text
    var typeQR: String = typeQR
    var image: ByteArray = image
}