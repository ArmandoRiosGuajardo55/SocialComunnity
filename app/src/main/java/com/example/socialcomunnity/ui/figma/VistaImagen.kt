package com.example.socialcomunnity.ui.figma

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Vista previa de una foto a pantalla completa, con zoom.
 *  - Pellizcar con dos dedos: acerca o aleja (hasta 5 veces).
 *  - Arrastrar con la foto ampliada: moverse por la foto.
 *  - Doble toque: acerca al punto tocado; otro doble toque vuelve al tamaño normal.
 *  - Un toque (sin zoom) o la X: cierra.
 */
@Composable
fun DialogoImagen(url: String, alCerrar: () -> Unit) {
    val urlNormal = urlImagenOptimizada(url)
    val urlGrande = urlImagenOptimizada(url, 1800)

    // La versión normal ya está en memoria (la de la publicación): se muestra al instante
    // mientras llega la versión grande, que se ve nítida al hacer zoom.
    val baja = remember(url) { CacheImagenes.obtener(urlNormal) }
    var fallo by remember(url) { mutableStateOf(false) }
    val alta by produceState<ImageBitmap?>(initialValue = null, key1 = url) {
        val cargada = withContext(Dispatchers.IO) {
            CacheImagenes.cargar(urlGrande) ?: CacheImagenes.cargar(urlNormal)
        }
        value = cargada
        if (cargada == null) fallo = true
    }
    val imagen = alta ?: baja

    var escala by remember { mutableStateOf(1f) }
    var desplazamiento by remember { mutableStateOf(Offset.Zero) }

    // Evita que la foto ampliada se salga de la pantalla
    fun limitar(valor: Offset, nuevaEscala: Float, ancho: Float, alto: Float): Offset {
        val maxX = ancho * (nuevaEscala - 1f) / 2f
        val maxY = alto * (nuevaEscala - 1f) / 2f
        return Offset(valor.x.coerceIn(-maxX, maxX), valor.y.coerceIn(-maxY, maxY))
    }

    Dialog(
        onDismissRequest = alCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { if (escala <= 1.05f) alCerrar() },
                        onDoubleTap = { toque ->
                            if (escala > 1.05f) {
                                escala = 1f
                                desplazamiento = Offset.Zero
                            } else {
                                val nueva = 2.5f
                                val centro = Offset(size.width / 2f, size.height / 2f)
                                escala = nueva
                                desplazamiento = limitar(
                                    (toque - centro) * (1f - nueva),
                                    nueva,
                                    size.width.toFloat(),
                                    size.height.toFloat()
                                )
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { centroide, desplazar, zoom, _ ->
                        val nueva = (escala * zoom).coerceIn(1f, 5f)
                        val centro = Offset(size.width / 2f, size.height / 2f)
                        val punto = centroide - centro
                        // El zoom se hace alrededor de los dedos, no del centro de la pantalla
                        val destino = (desplazamiento - punto) * (nueva / escala) + punto + desplazar
                        escala = nueva
                        desplazamiento = limitar(
                            destino,
                            nueva,
                            size.width.toFloat(),
                            size.height.toFloat()
                        )
                    }
                }
        ) {
            if (imagen != null) {
                Image(
                    bitmap = imagen,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = escala
                            scaleY = escala
                            translationX = desplazamiento.x
                            translationY = desplazamiento.y
                        }
                )
            } else if (fallo) {
                Text(
                    "No se pudo cargar la foto.",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                CircularProgressIndicator(
                    color = SC.Naranja,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            IconButton(
                onClick = alCerrar,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        }
    }
}
