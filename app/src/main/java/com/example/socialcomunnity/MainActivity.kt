
package com.example.socialcomunnity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialcomunnity.base.BaseDatosHelper
import com.example.socialcomunnity.datos.JsonHelper
import com.example.socialcomunnity.datos.XmlHelper
import com.example.socialcomunnity.modelo.Publicacion
import org.mindrot.jbcrypt.BCrypt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Naranja = Color(0xFFFF7900)
private val Fondo = Color(0xFF171717)
private val Blanco = Color(0xFFFFFFFF)
private val Gris = Color(0xFF292929)

private const val PASSWORD_HASH =
    "\$2a\$12\$fuSXJzQcGaFyNa20F3V1t.ZL3mADf4NCG.pWfGPOCseNt66Gu6JV6"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SocialComunnityApp()
            }
        }
    }
}

@Composable
fun SocialComunnityApp() {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var sesionIniciada by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Fondo
    ) {
        if (sesionIniciada) {
            PantallaBienvenida(
                usuario = usuario,
                cerrarSesion = {
                    sesionIniciada = false
                    usuario = ""
                    contrasena = ""
                    error = ""
                }
            )
        } else {
            PantallaLogin(
                usuario = usuario,
                cambiarUsuario = {
                    usuario = it
                    error = ""
                },
                contrasena = contrasena,
                cambiarContrasena = {
                    contrasena = it
                    error = ""
                },
                error = error,
                iniciarSesion = {
                    val accesoCorrecto = try {
                        usuario.trim() == "admin" &&
                                BCrypt.checkpw(contrasena, PASSWORD_HASH)
                    } catch (e: IllegalArgumentException) {
                        false
                    }

                    if (accesoCorrecto) {
                        sesionIniciada = true
                        error = ""
                    } else {
                        error = "Usuario o contraseña incorrectos"
                    }
                }
            )
        }
    }
}

@Composable
fun PantallaLogin(
    usuario: String,
    cambiarUsuario: (String) -> Unit,
    contrasena: String,
    cambiarContrasena: (String) -> Unit,
    error: String,
    iniciarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.imagen2),
            contentDescription = "Logo de SocialComunnity",
            modifier = Modifier.size(115.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SocialComunnity",
            color = Naranja,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Conecta con tu comunidad",
            color = Blanco,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Iniciar sesión",
            color = Blanco,
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = usuario,
            onValueChange = cambiarUsuario,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Usuario") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = Color.LightGray,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = cambiarContrasena,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = Color.LightGray,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = Color.LightGray
            )
        )

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error,
                color = Color(0xFFFF6B6B),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = iniciarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja,
                contentColor = Blanco
            )
        ) {
            Text(
                text = "Iniciar sesión",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Bienvenido a tu comunidad",
            color = Color.LightGray,
            fontSize = 13.sp
        )
    }
}

@Composable
fun PantallaBienvenida(
    usuario: String,
    cerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val baseDatos = remember { BaseDatosHelper(context) }

    var contenido by remember { mutableStateOf("") }
    var publicaciones by remember { mutableStateOf(emptyList<Publicacion>()) }
    var idEditando by remember { mutableStateOf<Int?>(null) }
    var resultadoExportacion by remember { mutableStateOf("") }
    var contenidoParaGuardar by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val guardarJson = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
                    it.write(contenidoParaGuardar)
                }
                mensaje = "Archivo JSON guardado correctamente"
            } catch (e: Exception) {
                mensaje = "Error al guardar el archivo JSON"
            }
        }
    }

    val guardarXml = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/xml")
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
                    it.write(contenidoParaGuardar)
                }
                mensaje = "Archivo XML guardado correctamente"
            } catch (e: Exception) {
                mensaje = "Error al guardar el archivo XML"
            }
        }
    }

    fun cargarPublicaciones() {
        publicaciones = baseDatos.obtenerTodas()
    }

    LaunchedEffect(Unit) {
        cargarPublicaciones()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.imagen2),
            contentDescription = "Logo de SocialComunnity",
            modifier = Modifier.size(90.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "¡Hola, $usuario!",
            color = Naranja,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Comunidad de publicaciones",
            color = Blanco,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (idEditando == null) "Crear publicación"
            else "Editar publicación",
            color = Naranja,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contenido,
            onValueChange = { contenido = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("¿Qué quieres compartir?") },
            minLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Blanco,
                unfocusedTextColor = Blanco,
                focusedLabelColor = Naranja,
                unfocusedLabelColor = Color.LightGray,
                cursorColor = Naranja,
                focusedBorderColor = Naranja,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (contenido.isNotBlank()) {
                    val fecha = SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date())

                    if (idEditando == null) {
                        baseDatos.insertar(
                            Publicacion(
                                usuario = usuario,
                                contenido = contenido.trim(),
                                fecha = fecha
                            )
                        )
                        mensaje = "Publicación creada"
                    } else {
                        baseDatos.actualizar(
                            Publicacion(
                                id = idEditando!!,
                                usuario = usuario,
                                contenido = contenido.trim(),
                                fecha = fecha
                            )
                        )
                        mensaje = "Publicación actualizada"
                    }

                    contenido = ""
                    idEditando = null
                    cargarPublicaciones()
                    resultadoExportacion = ""
                } else {
                    mensaje = "Escribe algo antes de publicar"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text(
                text = if (idEditando == null) "Publicar"
                else "Guardar cambios",
                color = Blanco
            )
        }

        if (idEditando != null) {
            TextButton(
                onClick = {
                    idEditando = null
                    contenido = ""
                    mensaje = ""
                }
            ) {
                Text("Cancelar edición", color = Blanco)
            }
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(mensaje, color = Naranja)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mis publicaciones",
            color = Blanco,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        if (publicaciones.isEmpty()) {
            Text(
                text = "Todavía no hay publicaciones.",
                color = Color.LightGray,
                modifier = Modifier.padding(16.dp)
            )
        }

        publicaciones.forEach { publicacion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Gris
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = publicacion.usuario,
                        color = Naranja,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = publicacion.contenido,
                        color = Blanco
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = publicacion.fecha,
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                idEditando = publicacion.id
                                contenido = publicacion.contenido
                                mensaje = ""
                            }
                        ) {
                            Text("Editar", color = Naranja)
                        }

                        TextButton(
                            onClick = {
                                baseDatos.eliminar(publicacion.id)
                                cargarPublicaciones()
                                resultadoExportacion = ""
                                mensaje = "Publicación eliminada"
                            }
                        ) {
                            Text("Eliminar", color = Color(0xFFFF6B6B))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Exportar publicaciones",
            color = Naranja,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                resultadoExportacion =
                    JsonHelper.convertirAJson(publicaciones)
                contenidoParaGuardar = resultadoExportacion
                mensaje = "JSON generado. Puedes guardarlo."
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gris
            )
        ) {
            Text("Ver formato JSON", color = Blanco)
        }

        Button(
            onClick = {
                resultadoExportacion =
                    XmlHelper.convertirAXml(publicaciones)
                contenidoParaGuardar = resultadoExportacion
                mensaje = "XML generado. Puedes guardarlo."
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gris
            )
        ) {
            Text("Ver formato XML", color = Blanco)
        }

        if (resultadoExportacion.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Contenido generado",
                color = Naranja,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Gris, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = resultadoExportacion,
                    color = Color(0xFF8FE3A0),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    guardarJson.launch("publicaciones.json")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Naranja
                )
            ) {
                Text("Guardar archivo JSON", color = Blanco)
            }

            Button(
                onClick = {
                    guardarXml.launch("publicaciones.xml")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Naranja
                )
            ) {
                Text("Guardar archivo XML", color = Blanco)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = cerrarSesion,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            )
        ) {
            Text("Cerrar sesión", color = Blanco)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
