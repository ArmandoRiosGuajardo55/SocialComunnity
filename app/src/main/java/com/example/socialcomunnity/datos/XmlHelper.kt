package com.example.socialcomunnity.datos

import com.example.socialcomunnity.modelo.Publicacion
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.StringReader
import java.io.StringWriter

object XmlHelper {

    fun convertirAXml(publicaciones: List<Publicacion>): String {
        val escritor = StringWriter()
        val serializer: XmlSerializer =
            XmlPullParserFactory.newInstance().newSerializer()

        serializer.setOutput(escritor)
        serializer.startDocument("UTF-8", true)
        serializer.startTag(null, "publicaciones")

        publicaciones.forEach { publicacion ->
            serializer.startTag(null, "publicacion")

            serializer.startTag(null, "id")
            serializer.text(publicacion.id.toString())
            serializer.endTag(null, "id")

            serializer.startTag(null, "usuario")
            serializer.text(publicacion.usuario)
            serializer.endTag(null, "usuario")

            serializer.startTag(null, "contenido")
            serializer.text(publicacion.contenido)
            serializer.endTag(null, "contenido")

            serializer.startTag(null, "fecha")
            serializer.text(publicacion.fecha)
            serializer.endTag(null, "fecha")

            serializer.endTag(null, "publicacion")
        }

        serializer.endTag(null, "publicaciones")
        serializer.endDocument()

        return escritor.toString()
    }
}

