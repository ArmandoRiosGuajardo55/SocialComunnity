

package com.example.socialcomunnity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mindrot.jbcrypt.BCrypt

private val Naranja = Color(0xFFFF7900)
private val NaranjaClaro = Color(0xFFFFA64D)
private val FondoOscuro = Color(0xFF24150D)
private val FondoSecundario = Color(0xFF422313)
private val TextoClaro = Color(0xFFFFF7F0)
private val TextoSecundario = Color(0xFFD9BBA7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PantallaLogin()
            }
        }
    }
}

@Composable
fun PantallaLogin() {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var accesoConcedido by remember { mutableStateOf(false) }

    // Estados para la demostración BCrypt
    var hashGenerado by remember { mutableStateOf("") }
    var mostrarHash by remember { mutableStateOf(false) }
    var errorDemo by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FondoOscuro,
                        FondoSecundario,
                        Color(0xFF1B100B)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (accesoConcedido) {
            PantallaBienvenida(
                usuario = usuario,
                cerrarSesion = {
                    accesoConcedido = false
                    usuario = ""
                    contrasena = ""
                    error = false
                    mostrarHash = false
                    hashGenerado = ""
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    painter = painterResource(
                        id = R.drawable.imagen2
                    ),
                    contentDescription = "Logo de SocialComunnity",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SocialComunnity",
                    color = NaranjaClaro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Conecta con tu comunidad",
                    color = TextoSecundario,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Usuario a la izquierda de su caja
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Usuario",
                        color = NaranjaClaro,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(86.dp)
                    )

                    OutlinedTextField(
                        value = usuario,
                        onValueChange = {
                            usuario = it
                            error = false
                        },
                        placeholder = {
                            Text(
                                "Escribe tu usuario",
                                color = TextoSecundario,
                                fontSize = 13.sp
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = campoPersonalizado()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contraseña a la izquierda de su caja
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Contraseña",
                        color = NaranjaClaro,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(86.dp)
                    )

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = {
                            contrasena = it
                            error = false
                            mostrarHash = false
                            hashGenerado = ""
                            errorDemo = false
                        },
                        placeholder = {
                            Text(
                                "Escribe tu contraseña",
                                color = TextoSecundario,
                                fontSize = 13.sp
                            )
                        },
                        singleLine = true,
                        visualTransformation =
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = campoPersonalizado()
                    )
                }

                if (error) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Usuario o contraseña incorrectos",
                        color = Color(0xFFFF7777),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (
                            usuario.trim() == "admin" &&
                            contrasena == "123456"
                        ) {
                            error = false
                            accesoConcedido = true
                        } else {
                            error = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Naranja
                    )
                ) {
                    Text(
                        text = "Iniciar sesión",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón de demostración BCrypt
                OutlinedButton(
                    onClick = {
                        if (contrasena.isNotBlank()) {
                            hashGenerado = BCrypt.hashpw(
                                contrasena,
                                BCrypt.gensalt(12)
                            )
                            mostrarHash = true
                            errorDemo = false
                        } else {
                            mostrarHash = false
                            errorDemo = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NaranjaClaro
                    )
                ) {
                    Text("Ver contraseña y hash")
                }

                if (errorDemo) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Escribe una contraseña primero.",
                        color = Color(0xFFFF7777),
                        fontSize = 13.sp
                    )
                }

                if (mostrarHash) {
                    Spacer(modifier = Modifier.height(18.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF352015)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Demostración BCrypt",
                                color = NaranjaClaro,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Contraseña original:",
                                color = TextoSecundario,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = contrasena,
                                color = TextoClaro
                            )

                            HorizontalDivider(
                                color = Naranja.copy(alpha = 0.5f)
                            )

                            Text(
                                text = "Hash BCrypt:",
                                color = TextoSecundario,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = hashGenerado,
                                color = NaranjaClaro,
                                fontSize = 12.sp
                            )

                            Text(
                                text = "Si vuelves a generar el hash, " +
                                        "puede cambiar por la sal aleatoria.",
                                color = TextoSecundario,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun PantallaBienvenida(
    usuario: String,
    cerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Bienvenido, $usuario!",
            color = NaranjaClaro,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ya estás dentro de SocialComunnity",
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = cerrarSesion,
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Cerrar sesión", color = Color.White)
        }
    }
}

@Composable
private fun campoPersonalizado() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextoClaro,
        unfocusedTextColor = TextoClaro,
        focusedBorderColor = Naranja,
        unfocusedBorderColor = NaranjaClaro,
        cursorColor = Naranja,
        focusedContainerColor = Color(0x332D1B11),
        unfocusedContainerColor = Color(0x332D1B11)
    )