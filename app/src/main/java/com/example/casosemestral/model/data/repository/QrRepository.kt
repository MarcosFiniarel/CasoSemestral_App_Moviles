package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.data.camera.QrResult

class QrRepository {
    fun processQrContent(content: String): QrResult {
        return QrResult(content)
    }
}