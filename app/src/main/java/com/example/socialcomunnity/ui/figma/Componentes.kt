package com.example.socialcomunnity.ui.figma

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialcomunnity.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// ---------------------------------------------------------------- Marca

/** Logo pequeño (burbuja "SC"): pantallas de login y registro. [tamano] es el alto. */
@Composable
fun LogoSC(tamano: Dp = 64.dp) {
    Image(
        painter = painterResource(R.drawable.logo_sc),
        contentDescription = "Social Community",
        modifier = Modifier
            .height(tamano)
            .aspectRatio(640f / 482f),
        contentScale = ContentScale.Fit
    )
}

/** Logo largo (burbuja + nombre): encabezado de la aplicación. [alto] es el alto. */
@Composable
fun LogoLargo(alto: Dp = 32.dp) {
    Image(
        painter = painterResource(R.drawable.logo_social_community),
        contentDescription = "Social Community",
        modifier = Modifier
            .height(alto)
            .aspectRatio(1200f / 231f),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun EtiquetaMarca() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF3A2A20))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "SOCIAL COMMUNITY",
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ---------------------------------------------------------------- Formularios

@Composable
fun CampoSC(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    marcador: String,
    icono: ImageVector,
    esContrasena: Boolean = false,
    mostrar: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = SC.Texto
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiar,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(marcador, color = SC.TextoSec, fontSize = 14.sp) },
            leadingIcon = { Icon(icono, contentDescription = null, tint = SC.TextoSec) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            visualTransformation = if (esContrasena && !mostrar) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SC.Naranja,
                unfocusedBorderColor = SC.Borde,
                focusedContainerColor = SC.Campo,
                unfocusedContainerColor = SC.Campo,
                cursorColor = SC.Naranja,
                focusedTextColor = SC.Texto,
                unfocusedTextColor = SC.Texto
            )
        )
    }
}

/**
 * Campo de fecha: al tocarlo se abre un calendario para elegir el día,
 * así nadie tiene que adivinar el formato. [alElegir] recibe la fecha como dd/MM/aaaa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFechaSC(
    etiqueta: String,
    valor: String,
    alElegir: (String) -> Unit,
    marcador: String,
    icono: ImageVector
) {
    var abierto by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = SC.Texto
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(marcador, color = SC.TextoSec, fontSize = 14.sp) },
                leadingIcon = { Icon(icono, contentDescription = null, tint = SC.TextoSec) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SC.Naranja,
                    unfocusedBorderColor = SC.Borde,
                    focusedContainerColor = SC.Campo,
                    unfocusedContainerColor = SC.Campo,
                    focusedTextColor = SC.Texto,
                    unfocusedTextColor = SC.Texto
                )
            )
            // Capa transparente que recibe el toque y abre el calendario
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { abierto = true }
            )
        }
    }

    if (abierto) {
        val anioActual = Calendar.getInstance().get(Calendar.YEAR)
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = milisDeFecha(valor),
            initialDisplayedMonthMillis = milisDeFecha(valor) ?: 946_684_800_000L, // enero de 2000
            yearRange = 1920..anioActual,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis <= System.currentTimeMillis()

                override fun isSelectableYear(year: Int): Boolean = year <= anioActual
            }
        )
        val colores = DatePickerDefaults.colors(
            containerColor = SC.Superficie,
            titleContentColor = SC.TextoSec,
            headlineContentColor = SC.Texto,
            weekdayContentColor = SC.TextoSec,
            subheadContentColor = SC.TextoSec,
            yearContentColor = SC.Texto,
            currentYearContentColor = SC.Naranja,
            selectedYearContentColor = Color.White,
            selectedYearContainerColor = SC.Naranja,
            dayContentColor = SC.Texto,
            selectedDayContentColor = Color.White,
            selectedDayContainerColor = SC.Naranja,
            todayContentColor = SC.Naranja,
            todayDateBorderColor = SC.Naranja
        )
        DatePickerDialog(
            onDismissRequest = { abierto = false },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { alElegir(textoDeFecha(it)) }
                    abierto = false
                }) {
                    Text("Aceptar", color = SC.Naranja, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { abierto = false }) {
                    Text("Cancelar", color = SC.TextoSec)
                }
            },
            colors = colores
        ) {
            DatePicker(state = estado, colors = colores)
        }
    }
}

private fun formatoFecha(): SimpleDateFormat =
    SimpleDateFormat("dd/MM/yyyy", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
        isLenient = false
    }

private fun textoDeFecha(milis: Long): String = formatoFecha().format(Date(milis))

private fun milisDeFecha(texto: String): Long? = try {
    formatoFecha().parse(texto)?.time
} catch (e: Exception) {
    null
}

@Composable
fun BotonSC(
    texto: String,
    alClic: () -> Unit,
    modifier: Modifier = Modifier,
    principal: Boolean = true,
    habilitado: Boolean = true,
    flecha: Boolean = false
) {
    Button(
        onClick = alClic,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (principal) SC.Naranja else SC.NaranjaSuave,
            contentColor = if (principal) Color.White else SC.Naranja,
            disabledContainerColor = Color(0xFFFFC9A0),
            disabledContentColor = Color.White
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        if (flecha) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun ChipSC(texto: String, seleccionado: Boolean, icono: ImageVector? = null, alClic: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (seleccionado) SC.Naranja else SC.NaranjaSuave)
            .clickable { alClic() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        val colorTexto = if (seleccionado) Color.White else SC.Naranja
        if (icono != null) {
            TextoConIcono(texto, icono, colorTexto, 12f, FontWeight.Medium)
        } else {
            Text(
                text = texto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colorTexto
            )
        }
    }
}

// ---------------------------------------------------------------- Estructura

@Composable
fun CabeceraNaranja(alNotificaciones: (() -> Unit)? = null, pendientes: Int = 0) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SC.Naranja)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LogoLargo(34.dp)
        Spacer(modifier = Modifier.weight(1f))
        if (alNotificaciones != null) {
            Box {
                IconButton(onClick = alNotificaciones) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White)
                }
                if (pendientes > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (pendientes > 9) "9+" else "$pendientes",
                            color = SC.NaranjaOscuro,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BarraNavegacion(actual: String, alIr: (String) -> Unit) {
    Surface(color = SC.Superficie, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ItemNavegacion("inicio", "Inicio", Icons.Default.Home, actual, alIr)
            ItemNavegacion("videos", "Videos", Icons.Default.PlayArrow, actual, alIr)

            Column(
                modifier = Modifier.clickable { alIr("crear") },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SC.Naranja),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear", tint = Color.White)
                }
                Text(
                    text = "Crear",
                    fontSize = 10.sp,
                    color = if (actual == "crear") SC.Naranja else SC.TextoSec
                )
            }

            ItemNavegacion("buscar", "Buscar", Icons.Default.Search, actual, alIr)
            ItemNavegacion("perfil", "Perfil", Icons.Default.Person, actual, alIr)
        }
    }
}

@Composable
private fun ItemNavegacion(
    ruta: String,
    texto: String,
    icono: ImageVector,
    actual: String,
    alIr: (String) -> Unit
) {
    val activo = actual == ruta
    val color = if (activo) SC.Naranja else SC.TextoSec
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { alIr(ruta) }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icono, contentDescription = texto, tint = color, modifier = Modifier.size(24.dp))
        Text(
            text = texto,
            fontSize = 10.sp,
            color = color,
            fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ---------------------------------------------------------------- Contenido

@Composable
fun Avatar(nombre: String, tamano: Dp = 40.dp, uid: String = "") {
    val foto = LocalFotos.current[uid]
    if (uid.isNotBlank() && !foto.isNullOrBlank()) {
        ImagenRemota(
            url = urlFotoPerfil(foto),
            modifier = Modifier
                .size(tamano)
                .clip(CircleShape)
        )
        return
    }
    val colores = listOf(
        Color(0xFFFF9A4D), Color(0xFF4D96FF), Color(0xFF3CB371),
        Color(0xFFB36BFF), Color(0xFFE5547A)
    )
    val iniciales = nombre.trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
    Box(
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(colores[nombre.length % colores.size]),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = iniciales,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (tamano.value * 0.38f).sp
        )
    }
}

@Composable
fun ImagenPartido(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF1B3A6B), Color(0xFF0B1A33)))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("RESULTADO FINAL", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp)
            Text("0-3", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text("Una noche para recordar.", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
        }
        Box(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.TopStart)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.9f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text("FÚTBOL · JORNADA 4", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SC.Texto)
        }
    }
}

@Composable
fun MiniaturaVideo(
    duracion: String,
    modifier: Modifier = Modifier,
    alto: Dp = 110.dp,
    conBotonGrande: Boolean = false
) {
    Box(
        modifier = modifier
            .height(alto)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF5A4A3C), Color(0xFF2B2119)))),
        contentAlignment = Alignment.Center
    ) {
        if (conBotonGrande) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Reproducir", tint = SC.Naranja)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(duracion, color = Color.White, fontSize = 9.sp)
        }
    }
}

@Composable
fun TarjetaPublicacion(post: PostUi, modifier: Modifier = Modifier) {
    var verVideo by remember { mutableStateOf(false) }
    var verImagen by remember { mutableStateOf(false) }
    var verComentarios by remember { mutableStateOf(false) }
    var menuAbierto by remember { mutableStateOf(false) }
    var confirmarBorrado by remember { mutableStateOf(false) }
    val acciones = LocalAcciones.current
    val contexto = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SC.Borde, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = SC.Superficie
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = acciones != null && post.autorUid.isNotBlank()) {
                            acciones?.alAbrirPerfil(post.autorUid)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(post.usuario, 38.dp, post.autorUid)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(post.usuario, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SC.Texto)
                        Text(post.fecha, fontSize = 11.sp, color = SC.TextoSec)
                    }
                }
                val esMia = acciones != null && acciones.uid.isNotBlank() && acciones.uid == post.autorUid
                Box {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Más opciones",
                        tint = SC.TextoSec,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { menuAbierto = true }
                            .padding(4.dp)
                    )
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        if (!esMia && post.autorUid.isNotBlank()) {
                            DropdownMenuItem(
                                text = { Text("Ver perfil") },
                                onClick = {
                                    menuAbierto = false
                                    acciones?.alAbrirPerfil(post.autorUid)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Compartir") },
                            onClick = {
                                menuAbierto = false
                                compartirPublicacion(contexto, post)
                            }
                        )
                        if (post.texto.isNotBlank()) {
                            DropdownMenuItem(
                                text = { Text("Copiar texto") },
                                onClick = {
                                    menuAbierto = false
                                    copiarTexto(contexto, post.texto)
                                }
                            )
                        }
                        if (esMia && post.id.isNotBlank()) {
                            DropdownMenuItem(
                                text = { Text("Eliminar publicación", color = Color(0xFFD93025)) },
                                onClick = {
                                    menuAbierto = false
                                    confirmarBorrado = true
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (post.texto.isNotBlank()) {
                Text(post.texto, fontSize = 14.sp, color = SC.Texto)
            }
            if (post.mediaUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                if (post.mediaTipo == "video") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black)
                            .clickable { verVideo = true },
                        contentAlignment = Alignment.Center
                    ) {
                        ImagenRemota(urlPortadaVideo(post.mediaUrl), Modifier.fillMaxSize())
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Reproducir", tint = SC.Naranja)
                        }
                    }
                } else {
                    ImagenRemota(
                        url = urlImagenOptimizada(post.mediaUrl),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { verImagen = true }
                    )
                }
            }
            if (post.conImagen) {
                Spacer(modifier = Modifier.height(10.dp))
                ImagenPartido()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Me gusta
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = acciones != null && post.id.isNotBlank()) {
                            acciones?.alAlternarMeGusta(post)
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (post.meGusta) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Me gusta",
                        tint = SC.Naranja,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.likes}", fontSize = 12.sp, color = SC.TextoSec)
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Comentarios
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = post.id.isNotBlank()) { verComentarios = true }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        IconoComentario,
                        contentDescription = "Comentarios",
                        tint = SC.TextoSec,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${post.comentarios}", fontSize = 12.sp, color = SC.TextoSec)
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Compartir con otras apps
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { compartirPublicacion(contexto, post) }
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir", tint = SC.TextoSec, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    if (verVideo) {
        DialogoVideo(post.mediaUrl) { verVideo = false }
    }
    if (verImagen) {
        DialogoImagen(post.mediaUrl) { verImagen = false }
    }
    if (verComentarios) {
        HojaComentarios(post) { verComentarios = false }
    }
    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("¿Eliminar esta publicación?") },
            text = { Text("Se borrará para toda la comunidad y no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarBorrado = false
                    acciones?.alEliminar(post)
                }) {
                    Text("Eliminar", color = Color(0xFFD93025), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) {
                    Text("Cancelar", color = SC.TextoSec)
                }
            }
        )
    }
}

@Composable
fun EncabezadoSeccion(titulo: String, accion: String? = null, alAccion: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SC.Texto)
        if (accion != null) {
            Text(
                text = accion,
                fontSize = 12.sp,
                color = SC.Naranja,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { alAccion() }
            )
        }
    }
}
