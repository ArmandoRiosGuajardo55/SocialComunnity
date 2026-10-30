package com.example.socialcomunnity.datos

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import org.mindrot.jbcrypt.BCrypt
import java.io.File

class UsuarioJsonHelper(context: Context) {

    private val archivo = File(context.filesDir, "usuarios.json")

    companion object {
        private const val ADMIN_HASH =
            "\$2a\$12\$fuSXJzQcGaFyNa20F3V1t.ZL3mADf4NCG.pWfGPOCseNt66Gu6JV6"
    }

    private fun leerUsuarios(): JSONArray {
        if (!archivo.exists()) {
            return JSONArray()
        }

        val contenido = archivo.readText()
        val json = JSONObject(contenido)

        return json.optJSONArray("usuarios") ?: JSONArray()
    }

    private fun guardarUsuarios(usuarios: JSONArray) {
        val json = JSONObject()
        json.put("usuarios", usuarios)

        archivo.writeText(json.toString(4))
    }

    // Conserva la cuenta admin que ya teníamos
    fun inicializarAdmin() {
        if (!archivo.exists()) {
            val usuarios = JSONArray()

            val admin = JSONObject()
            admin.put("usuario", "admin")
            admin.put("passwordHash", ADMIN_HASH)

            usuarios.put(admin)
            guardarUsuarios(usuarios)
        }
    }

    // Registrar una cuenta nueva
    fun registrarUsuario(
        usuario: String,
        contrasena: String
    ): Boolean {
        val nombre = usuario.trim()

        if (nombre.isBlank() || contrasena.isBlank()) {
            return false
        }

        return try {
            val usuarios = leerUsuarios()

            for (i in 0 until usuarios.length()) {
                val existente = usuarios.getJSONObject(i)
                if (existente.getString("usuario")
                        .equals(nombre, ignoreCase = true)
                ) {
                    return false
                }
            }

            val nuevoUsuario = JSONObject()
            nuevoUsuario.put("usuario", nombre)
            nuevoUsuario.put(
                "passwordHash",
                BCrypt.hashpw(contrasena, BCrypt.gensalt(12))
            )

            usuarios.put(nuevoUsuario)
            guardarUsuarios(usuarios)

            true
        } catch (e: Exception) {
            false
        }
    }

    // Comprobar el inicio de sesión
    fun validarUsuario(
        usuario: String,
        contrasena: String
    ): Boolean {
        return try {
            val usuarios = leerUsuarios()
            val nombre = usuario.trim()

            for (i in 0 until usuarios.length()) {
                val cuenta = usuarios.getJSONObject(i)

                if (cuenta.getString("usuario")
                        .equals(nombre, ignoreCase = true)
                ) {
                    return BCrypt.checkpw(
                        contrasena,
                        cuenta.getString("passwordHash")
                    )
                }
            }

            false
        } catch (e: Exception) {
            false
        }
    }
}

