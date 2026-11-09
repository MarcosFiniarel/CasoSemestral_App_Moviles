package com.example.casosemestral.model.data.repository

import com.example.casosemestral.model.camera.QrResult

class QrRepository {
    fun processQrContent(content: String): QrResult {
        return QrResult(content)
    }
}