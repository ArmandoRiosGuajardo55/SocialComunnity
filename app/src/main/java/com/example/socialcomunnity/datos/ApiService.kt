
package com.example.socialcomunnity.datos

import com.example.socialcomunnity.modelo.PublicacionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Cliente HTTP para JSONPlaceholder.
 * OkHttp permite utilizar GET, POST, PUT, PATCH y DELETE.
 */
object ApiService {

    private const val BASE_URL =
        "https://jsonplaceholder.typicode.com/posts"

    private val tipoJson =
        "application/json; charset=utf-8".toMediaType()

    private val cliente = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private suspend fun solicitar(
        metodo: String,
        id: Int? = null,
        datos: JSONObject? = null
    ): String = withContext(Dispatchers.IO) {

        val direccion = if (id != null) {
            "$BASE_URL/$id"
        } else {
            BASE_URL
        }

        val constructor = Request.Builder()
            .url(direccion)
            .header("Accept", "application/json")

        if (datos != null) {
            val cuerpo = datos.toString().toRequestBody(tipoJson)
            constructor.method(metodo, cuerpo)
        } else if (metodo == "DELETE") {
            constructor.delete()
        } else {
            constructor.method(metodo, null)
        }

        cliente.newCall(constructor.build()).execute().use { response ->

            val texto = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                throw IOException(
                    "Error HTTP ${response.code}: " +
                            texto.ifBlank { response.message }
                )
            }

            texto
        }
    }

    // GET: consulta una publicación.
    suspend fun obtenerPublicacion(): PublicacionApi {
        val respuesta = solicitar("GET", 1)
        val json = JSONObject(respuesta)

        return PublicacionApi(
            userId = json.getInt("userId"),
            id = json.getInt("id"),
            title = json.getString("title"),
            body = json.getString("body")
        )
    }

    // POST: crea una publicación.
    suspend fun crearPublicacion(
        titulo: String,
        contenido: String
    ): String {

        require(titulo.isNotBlank()) {
            "El título no puede estar vacío."
        }

        require(contenido.isNotBlank()) {
            "La descripción no puede estar vacía."
        }

        val json = JSONObject().apply {
            put("userId", 1)
            put("title", titulo.trim())
            put("body", contenido.trim())
        }

        return solicitar("POST", datos = json)
    }

    // PUT: reemplaza los datos de una publicación.
    suspend fun actualizarPublicacion(
        id: Int,
        titulo: String,
        contenido: String
    ): String {

        require(id > 0) {
            "El ID debe ser válido."
        }

        require(titulo.isNotBlank()) {
            "El título no puede estar vacío."
        }

        require(contenido.isNotBlank()) {
            "La descripción no puede estar vacía."
        }

        val json = JSONObject().apply {
            put("userId", 1)
            put("id", id)
            put("title", titulo.trim())
            put("body", contenido.trim())
        }

        return solicitar("PUT", id, json)
    }

    // PATCH: modifica únicamente los campos proporcionados.
    suspend fun modificarParcialmente(
        id: Int,
        nuevoTitulo: String,
        nuevaDescripcion: String
    ): String {

        require(id > 0) {
            "El ID debe ser válido."
        }

        require(
            nuevoTitulo.isNotBlank() ||
                    nuevaDescripcion.isNotBlank()
        ) {
            "Escribe un título o una descripción para modificar."
        }

        val json = JSONObject().apply {
            if (nuevoTitulo.isNotBlank()) {
                put("title", nuevoTitulo.trim())
            }

            if (nuevaDescripcion.isNotBlank()) {
                put("body", nuevaDescripcion.trim())
            }
        }

        return solicitar("PATCH", id, json)
    }

    // DELETE: solicita la eliminación de una publicación.
    suspend fun eliminarPublicacion(id: Int): String {

        require(id > 0) {
            "El ID debe ser válido."
        }

        return solicitar("DELETE", id)
    }
}
