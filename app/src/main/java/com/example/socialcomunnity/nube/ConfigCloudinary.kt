package com.example.socialcomunnity.nube

/**
 * Datos de tu cuenta de Cloudinary. Cambia los dos textos entre comillas:
 *  - NOMBRE_NUBE: el "Cloud name" que aparece en tu panel de Cloudinary.
 *  - PRESET: el nombre del "Upload preset" (de tipo Unsigned) que creaste.
 */
object ConfigCloudinary {
    const val NOMBRE_NUBE = "aeebkf8e"
    const val PRESET = "socialcommunity"

    val configurado: Boolean
        get() = NOMBRE_NUBE.isNotBlank() && PRESET.isNotBlank()
}
