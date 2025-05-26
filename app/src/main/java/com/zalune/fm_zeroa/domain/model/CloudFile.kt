package com.zalune.fm_zeroa.domain.model

data class CloudFile(
    val id: String, // Puede ser la URL o ID del archivo
    val name: String,
    val size: Long?, // En bytes, si está disponible
    val url: String?, // URL pública para descarga si aplica
    val provider: CloudProvider // Enum para saber de dónde viene
)