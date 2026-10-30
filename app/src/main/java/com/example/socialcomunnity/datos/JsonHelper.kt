package com.example.socialcomunnity.datos

import com.example.socialcomunnity.modelo.Publicacion
import org.json.JSONArray
import org.json.JSONObject

object JsonHelper {

    fun convertirAJson(publicaciones: List<Publicacion>): String {
        val jsonArray = JSONArray()

        publicaciones.forEach { publicacion ->
            val objeto = JSONObject().apply {
                put("id", publicacion.id)
                put("usuario", publicacion.usuario)
                put("contenido", publicacion.contenido)
                put("fecha", publicacion.fecha)
            }

            jsonArray.put(objeto)
        }

        return jsonArray.toString(4)
    }
}