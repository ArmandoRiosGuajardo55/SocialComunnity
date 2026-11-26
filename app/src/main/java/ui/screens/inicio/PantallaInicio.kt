package com.example.socialcomunnity.ui.screens.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialcomunnity.base.BaseDatosHelper
import com.example.socialcomunnity.modelo.Publicacion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Naranja = Color(0xFFFF7900)
private val Fondo = Color(0xFFF7F7F7)
private val Blanco = Color(0xFFFFFFFF)
private val Texto = Color(0xFF222222)
private val TextoSecundario = Color(0xFF777777)
private val Borde = Color(0xFFEAEAEA)

// Publicaciones de demostración.
// Se muestran solamente cuando SQLite todavía está vacía.
private val publicacionesDemo = listOf(
    Publicacion(
        id = -1,
        usuario = "Carlos Mendoza",
        contenido = "¡Bienvenidos a nuestra comunidad! Este es nuestro espacio para compartir ideas.",
        fecha = "Hace 10 minutos"
    ),
    Publicacion(
        id = -2,
        usuario = "Ana López",
        contenido = "Hoy es un buen día para aprender algo nuevo y compartirlo con los demás.",
        fecha = "Hace 30 minutos"
    ),
    Publicacion(
        id = -3,
        usuario = "Daniel Torres",
        contenido = "¿Qué proyectos están desarrollando actualmente? Me gustaría conocer sus ideas.",
        fecha = "Hace 1 hora"
    )
)

@Composable
fun PantallaInicio(
    usuario: String,
    onAbrirNotificaciones: () -> Unit
) {
    val context = LocalContext.current
    val baseDatos = remember {
        BaseDatosHelper(context)
    }

    var textoPublicacion by remember {
        mutableStateOf("")
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    var publicaciones by remember {
        mutableStateOf<List<Publicacion>>(emptyList())
    }

    // Consultamos SQLite cada vez que se entra a esta pantalla.
    LaunchedEffect(Unit) {
        val guardadas = baseDatos.obtenerTodas()

        publicaciones = if (guardadas.isEmpty()) {
            publicacionesDemo
        } else {
            guardadas
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
    ) {

        // ENCABEZADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Naranja)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Tu comunidad",
                    color = Blanco,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Comparte lo que sucede",
                    color = Blanco.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = onAbrirNotificaciones
            ) {
                Text(
                    text = "♧",
                    color = Blanco,
                    fontSize = 25.sp
                )
            }
        }

        // LISTA PRINCIPAL
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Para ti",
                    color = Texto,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // TARJETA PARA CREAR PUBLICACIÓN
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Blanco
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarInicio(
                                inicial = usuario.firstOrNull()
                                    ?.uppercase() ?: "U",
                                tamano = 42
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "¿Qué estás pensando?",
                                color = TextoSecundario,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = textoPublicacion,
                            onValueChange = {
                                textoPublicacion = it
                                mensaje = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text("Escribe algo para tu comunidad...")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Naranja,
                                unfocusedBorderColor = Borde,
                                focusedTextColor = Texto,
                                unfocusedTextColor = Texto
                            )
                        )

                        if (mensaje.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = mensaje,
                                color = Naranja,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (textoPublicacion.isNotBlank()) {

                                    val fecha = SimpleDateFormat(
                                        "dd/MM/yyyy HH:mm",
                                        Locale.getDefault()
                                    ).format(Date())

                                    val nuevaPublicacion = Publicacion(
                                        usuario = usuario,
                                        contenido = textoPublicacion.trim(),
                                        fecha = fecha
                                    )

                                    val resultado = baseDatos.insertar(
                                        nuevaPublicacion
                                    )

                                    if (resultado != -1L) {
                                        publicaciones = baseDatos.obtenerTodas()
                                        textoPublicacion = ""
                                        mensaje = "¡Publicación guardada!"
                                    } else {
                                        mensaje = "No se pudo guardar la publicación."
                                    }

                                } else {
                                    mensaje = "Escribe algo antes de publicar."
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Naranja
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Publicar",
                                color = Blanco,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // PUBLICACIONES GUARDADAS
            items(
                items = publicaciones,
                key = { it.id }
            ) { publicacion ->
                TarjetaPublicacionInicio(
                    publicacion = publicacion
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AvatarInicio(
    inicial: String,
    tamano: Int
) {
    Box(
        modifier = Modifier
            .size(tamano.dp)
            .background(
                color = Naranja.copy(alpha = 0.15f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = inicial,
            color = Naranja,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
        )
    }
}

@Composable
fun TarjetaPublicacionInicio(
    publicacion: Publicacion
) {
    var liked by remember(publicacion.id) {
        mutableStateOf(false)
    }

    var likes by remember(publicacion.id) {
        mutableStateOf(0)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Blanco
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            // USUARIO Y FECHA
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarInicio(
                    inicial = publicacion.usuario.firstOrNull()
                        ?.uppercase() ?: "U",
                    tamano = 42
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = publicacion.usuario,
                        color = Texto,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = publicacion.fecha,
                        color = TextoSecundario,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "⋮",
                    color = TextoSecundario,
                    fontSize = 23.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CONTENIDO
            Text(
                text = publicacion.contenido,
                color = Texto,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(
                color = Borde,
                thickness = 1.dp
            )

            // INTERACCIONES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        liked = !liked
                        likes += if (liked) 1 else -1
                    }
                ) {
                    Text(
                        text = if (liked) {
                            "♥  $likes"
                        } else {
                            "♡  $likes"
                        },
                        color = if (liked) {
                            Naranja
                        } else {
                            TextoSecundario
                        },
                        fontSize = 12.sp
                    )
                }

                TextButton(
                    onClick = {}
                ) {
                    Text(
                        text = "○  Comentar",
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )
                }

                TextButton(
                    onClick = {}
                ) {
                    Text(
                        text = "↗  Compartir",
                        color = TextoSecundario,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}