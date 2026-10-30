package com.example.socialcomunnity.base

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.socialcomunnity.modelo.Publicacion

class BaseDatosHelper(context: Context) :
    SQLiteOpenHelper(context, "SocialComunnity.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE publicaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT NOT NULL,
                contenido TEXT NOT NULL,
                fecha TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Aquí agregaremos cambios futuros a la estructura de la base de datos.
    }

    fun insertar(publicacion: Publicacion): Long {
        val valores = ContentValues().apply {
            put("usuario", publicacion.usuario)
            put("contenido", publicacion.contenido)
            put("fecha", publicacion.fecha)
        }
        return writableDatabase.insert("publicaciones", null, valores)
    }

    fun obtenerTodas(): List<Publicacion> {
        val lista = mutableListOf<Publicacion>()
        val cursor = readableDatabase.query(
            "publicaciones",
            arrayOf("id", "usuario", "contenido", "fecha"),
            null, null, null, null,
            "id DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Publicacion(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        usuario = it.getString(it.getColumnIndexOrThrow("usuario")),
                        contenido = it.getString(it.getColumnIndexOrThrow("contenido")),
                        fecha = it.getString(it.getColumnIndexOrThrow("fecha"))
                    )
                )
            }
        }
        return lista
    }

    fun actualizar(publicacion: Publicacion): Int {
        val valores = ContentValues().apply {
            put("usuario", publicacion.usuario)
            put("contenido", publicacion.contenido)
            put("fecha", publicacion.fecha)
        }
        return writableDatabase.update(
            "publicaciones",
            valores,
            "id = ?",
            arrayOf(publicacion.id.toString())
        )
    }

    fun eliminar(id: Int): Int {
        return writableDatabase.delete(
            "publicaciones",
            "id = ?",
            arrayOf(id.toString())
        )
    }
}