
package com.example.socialcomunnity.ui.screens.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NaranjaNotificaciones = Color(0xFFFF7900)
private val FondoNotificaciones = Color(0xFFFFF8F4)
private val TextoNotificaciones = Color(0xFF252525)
private val GrisNotificaciones = Color(0xFF888888)

private data class Notificacion(
    val iniciales: String,
    val nombre: String,
    val accion: String,
    val detalle: String,
    val hora: String,
    val leida: Boolean,
    val tipo: String
)

@Composable
fun PantallaNotificaciones(
    onVolver: () -> Unit
) {
    val recientes = listOf(
        Notificacion(
            "LM", "Lucía Martínez",
            "y 12 personas más indicaron que les gusta tu publicación.",
            "Tu publicación", "Hace 5 min", false, "Publicaciones"
        ),
        Notificacion(
            "CG", "Carlos García",
            "comentó: ¡Qué gran partido!",
            "Tu publicación", "Hace 15 min", false, "Publicaciones"
        ),
        Notificacion(
            "AL", "Ana López",
            "comenzó a seguirte.",
            "", "Hace 1 h", false, "Personas"
        )
    )

    val anteriores = listOf(
        Notificacion(
            "DR", "Diego Ruiz",
            "compartió tu publicación de fútbol.",
            "", "Ayer · 18:42", true, "Publicaciones"
        ),
        Notificacion(
            "ST", "Sofía Torres",
            "te mencionó en un comentario.",
            "", "Ayer · 16:20", true, "Publicaciones"
        ),
        Notificacion(
            "MP", "Marcos Pérez",
            "indicó que le gusta tu publicación.",
            "", "Hace 2 días", true, "Publicaciones"
        )
    )

    var filtro by remember { mutableStateOf("Todas") }

    val todasLasNotificaciones = recientes + anteriores

    val notificacionesFiltradas = when (filtro) {
        "Personas" -> todasLasNotificaciones.filter {
            it.tipo == "Personas"
        }
        "Publicaciones" -> todasLasNotificaciones.filter {
            it.tipo == "Publicaciones"
        }
        else -> todasLasNotificaciones
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoNotificaciones)
    ) {
        // ENCABEZADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onVolver) {
                Text(
                    text = "‹",
                    fontSize = 32.sp,
                    color = NaranjaNotificaciones
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Notificaciones",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoNotificaciones
                )

                Text(
                    text = "Todo lo que pasa en tu comunidad.",
                    fontSize = 12.sp,
                    color = GrisNotificaciones
                )
            }

            Text(
                text = "☷",
                fontSize = 24.sp,
                color = TextoNotificaciones
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // FILTROS
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Todas",
                        "Personas",
                        "Publicaciones"
                    ).forEach { opcion ->

                        FilterChip(
                            selected = filtro == opcion,
                            onClick = {
                                filtro = opcion
                            },
                            label = {
                                Text(
                                    opcion,
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NaranjaNotificaciones,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Actividad de tu comunidad",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextoNotificaciones
                )
            }

            items(notificacionesFiltradas) { notificacion ->
                TarjetaNotificacion(notificacion)
            }

            if (notificacionesFiltradas.isEmpty()) {
                item {
                    Text(
                        "No hay notificaciones en esta categoría.",
                        color = GrisNotificaciones
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaNotificacion(
    notificacion: Notificacion
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // AVATAR
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        NaranjaNotificaciones.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = notificacion.iniciales,
                    color = NaranjaNotificaciones,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${notificacion.nombre} ${notificacion.accion}",
                    fontSize = 12.sp,
                    color = TextoNotificaciones,
                    lineHeight = 17.sp
                )

                if (notificacion.detalle.isNotBlank()) {
                    Text(
                        text = notificacion.detalle,
                        fontSize = 10.sp,
                        color = GrisNotificaciones
                    )
                }

                Text(
                    text = notificacion.hora,
                    fontSize = 10.sp,
                    color = GrisNotificaciones,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (!notificacion.leida) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            NaranjaNotificaciones,
                            CircleShape
                        )
                )
            }
        }
    }
}
