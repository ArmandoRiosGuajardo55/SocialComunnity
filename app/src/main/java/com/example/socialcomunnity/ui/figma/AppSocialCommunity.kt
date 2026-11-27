package com.example.socialcomunnity.ui.figma

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.socialcomunnity.nube.Medios
import com.example.socialcomunnity.nube.Nube
import com.example.socialcomunnity.nube.PerfilNube
import com.example.socialcomunnity.nube.UsuarioNube
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

// Pestañas por las que se pasa con un gesto horizontal. "Crear" queda fuera a propósito,
// para no perder un borrador al deslizar sin querer.
private val ORDEN_PESTANAS = listOf("inicio", "videos", "buscar", "perfil")

/**
 * Punto de entrada de la interfaz: decide qué pantalla se ve.
 * Las cuentas, los perfiles y las publicaciones viven en Firebase (ver [Nube]).
 */
@Composable
fun AppSocialCommunity() {
    val context = LocalContext.current
    val preferencias = remember { context.getSharedPreferences("sc_perfiles", Context.MODE_PRIVATE) }

    // Modo de apariencia: "claro", "oscuro" o "sistema" (se recuerda en el teléfono)
    var modoTema by remember { mutableStateOf(preferencias.getString("tema_app", "sistema") ?: "sistema") }
    SC.oscuro = when (modoTema) {
        "oscuro" -> true
        "claro" -> false
        else -> isSystemInDarkTheme()
    }

    // Los íconos de la barra de estado y de navegación se adaptan al modo elegido
    val vista = LocalView.current
    SideEffect {
        val ventana = (vista.context as? Activity)?.window
        if (ventana != null) {
            val controlador = WindowCompat.getInsetsController(ventana, vista)
            controlador.isAppearanceLightStatusBars = !SC.oscuro
            controlador.isAppearanceLightNavigationBars = !SC.oscuro
        }
    }

    // Sesión: Firebase recuerda la cuenta, así que si ya hay una abierta se entra directo
    var uidSesion by remember { mutableStateOf(Nube.uidActual()) }
    var correoSesion by remember { mutableStateOf(Nube.correoActual()) }
    // Si alguien ya inició sesión alguna vez en este teléfono, sin sesión se le muestra "Iniciar sesión";
    // si es la primera vez (app recién instalada), se le muestra "Crear cuenta".
    fun rutaSinSesion(): String =
        if (preferencias.getBoolean("ya_inicio_sesion", false)) "login" else "registro"

    // "login", "registro" o "app"
    var ruta by remember { mutableStateOf(if (Nube.uidActual().isNotBlank()) "app" else rutaSinSesion()) }
    // "inicio", "notificaciones", "videos", "crear", "buscar", "perfil" o "configuracion"
    var pestana by remember { mutableStateOf("inicio") }
    // Perfil de otra persona que se está viendo, y la pantalla a la que se vuelve
    var verPerfilUid by remember { mutableStateOf("") }
    var origenPerfil by remember { mutableStateOf("inicio") }

    var mensaje by remember { mutableStateOf("") }
    var mensajeEsError by remember { mutableStateOf(true) }
    var ocupado by remember { mutableStateOf(false) }

    var perfil by remember { mutableStateOf<PerfilNube?>(null) }
    var publicaciones by remember { mutableStateOf<List<PostUi>>(emptyList()) }
    var notificaciones by remember { mutableStateOf<List<NotiUi>>(emptyList()) }
    var usuarios by remember { mutableStateOf<List<UsuarioNube>>(emptyList()) }

    // Publicación (y comentario) que se abre al tocar una notificación
    var postDeNotificacion by remember { mutableStateOf<PostUi?>(null) }
    var comentarioDeNotificacion by remember { mutableStateOf("") }

    // Publicación que se muestra completa al tocar una notificación
    var postVista by remember { mutableStateOf<PostUi?>(null) }

    // Lista de seguidores / siguiendo de una cuenta
    var listaDeUid by remember { mutableStateOf("") }
    var listaTipo by remember { mutableStateOf("seguidores") }
    var origenLista by remember { mutableStateOf("perfil") }
    var perfilDeOrigenLista by remember { mutableStateOf("") }
    var abiertoDesdeLista by remember { mutableStateOf(false) }

    fun volverDeLista() {
        if (origenLista == "perfilAjeno") verPerfilUid = perfilDeOrigenLista
        abiertoDesdeLista = false
        pestana = origenLista
    }

    // Últimas búsquedas, guardadas en el teléfono
    fun leerRecientes(): List<String> =
        (preferencias.getString("busquedas", "") ?: "").split("|").filter { it.isNotBlank() }
    var recientes by remember { mutableStateOf(leerRecientes()) }
    fun guardarRecientes(lista: List<String>) {
        recientes = lista
        preferencias.edit().putString("busquedas", lista.joinToString("|")).apply()
    }

    // La barra inferior se oculta al bajar por una lista y reaparece al subir.
    // Solo se oculta si el contenido de verdad se desplaza: si todo cabe en la pantalla, la barra se queda.
    var barraVisible by remember { mutableStateOf(true) }
    val conexionScroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Dedo hacia abajo (subir por la lista): la barra vuelve, aunque ya no haya más por subir
                if (available.y > 2f) barraVisible = true
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // "consumed" es lo que la lista realmente se movió: si no hay nada que desplazar, es 0
                if (consumed.y < -2f) barraVisible = false
                return Offset.Zero
            }
        }
    }

    fun irAPestana(destino: String) {
        barraVisible = true
        pestana = destino
    }

    // Se entera cuando alguien inicia o cierra sesión
    DisposableEffect(Unit) {
        val dejarDeEscuchar = Nube.escucharSesion { uid, correo ->
            uidSesion = uid
            correoSesion = correo
            ocupado = false
            if (uid.isNotBlank()) {
                preferencias.edit().putBoolean("ya_inicio_sesion", true).apply()
                mensaje = ""
                pestana = "inicio"
                ruta = "app"
            } else {
                perfil = null
                publicaciones = emptyList()
                notificaciones = emptyList()
                usuarios = emptyList()
                ruta = rutaSinSesion()
            }
        }
        onDispose { dejarDeEscuchar() }
    }

    // Con la sesión abierta, el perfil y el feed se mantienen al día en tiempo real
    DisposableEffect(uidSesion) {
        var dejarPerfil: () -> Unit = {}
        var dejarPublicaciones: () -> Unit = {}
        var dejarNotificaciones: () -> Unit = {}
        var dejarUsuarios: () -> Unit = {}
        if (uidSesion.isNotBlank()) {
            dejarUsuarios = Nube.escucharUsuarios { usuarios = it }
            dejarPerfil = Nube.escucharPerfil(uidSesion) { perfil = it }
            dejarPublicaciones = Nube.escucharPublicaciones(uidSesion) { publicaciones = it }
            dejarNotificaciones = Nube.escucharNotificaciones(uidSesion) { notificaciones = it }
        }
        onDispose {
            dejarPerfil()
            dejarPublicaciones()
            dejarNotificaciones()
            dejarUsuarios()
        }
    }

    // Mientras se ve una publicación abierta desde una notificación, se mantiene al día en tiempo real
    // (aunque ya no esté entre las últimas del feed)
    DisposableEffect(pestana, postVista?.id, uidSesion) {
        val id = postVista?.id
        var dejarDeEscuchar: () -> Unit = {}
        if (pestana == "publicacion" && !id.isNullOrBlank() && uidSesion.isNotBlank()) {
            dejarDeEscuchar = Nube.escucharPublicacion(id, uidSesion) { postVista = it }
        }
        onDispose { dejarDeEscuchar() }
    }

    BackHandler(enabled = ruta == "app" && pestana != "inicio") {
        if (pestana == "lista") {
            volverDeLista()
        } else {
            pestana = when (pestana) {
                "configuracion" -> "perfil"
                "perfilAjeno" -> if (abiertoDesdeLista) "lista" else origenPerfil
                "publicacion" -> "notificaciones"
                else -> "inicio"
            }
        }
    }

    // Datos de la cuenta; mientras el perfil llega de la nube se usa el correo
    val alias = correoSesion.substringBefore("@")
    val nombreSesion = perfil?.nombre?.takeIf { it.isNotBlank() }
        ?: alias.replaceFirstChar { it.uppercase() }
    val handle = perfil?.usuario?.takeIf { it.isNotBlank() } ?: alias.lowercase()
    val bioSesion = perfil?.bio ?: ""
    // Seguidores, fotos de perfil y personas de la comunidad
    val miSiguiendo = usuarios.firstOrNull { it.uid == uidSesion }?.siguiendo ?: emptyList()
    val seguidores = usuarios.count { uidSesion in it.siguiendo }
    val fotos = usuarios.filter { it.foto.isNotBlank() }.associate { it.uid to it.foto }
    val personas = usuarios
        .filter { it.uid != uidSesion }
        .map {
            PersonaUi(
                nombre = it.nombre.ifBlank { it.usuario },
                usuario = "@${it.usuario}",
                detalle = it.bio.ifBlank { "Miembro de la comunidad" },
                uid = it.uid
            )
        }
    val notificacionesActivadas = perfil?.notificaciones ?: true
    val pendientes = if (notificacionesActivadas) notificaciones.count { it.nueva } else 0

    // Abre el perfil de una persona (el propio abre la pestaña Perfil)
    fun abrirPerfil(uid: String) {
        if (uid.isBlank()) return
        if (uid == uidSesion) {
            pestana = "perfil"
        } else {
            abiertoDesdeLista = pestana == "lista"
            if (pestana != "perfilAjeno" && pestana != "lista") origenPerfil = pestana
            verPerfilUid = uid
            pestana = "perfilAjeno"
        }
    }

    // Abre la lista de seguidores o de seguidos de una cuenta
    fun abrirLista(deUid: String, tipo: String) {
        origenLista = pestana
        perfilDeOrigenLista = verPerfilUid
        listaDeUid = deUid
        listaTipo = tipo
        pestana = "lista"
    }

    // Lo que las tarjetas de publicación pueden hacer: me gusta y comentarios
    val acciones = remember(uidSesion, nombreSesion) {
        AccionesPost(
            uid = uidSesion,
            alAlternarMeGusta = { Nube.alternarMeGusta(it, uidSesion, nombreSesion) },
            alComentar = { post, texto, respondeAUid ->
                Nube.comentar(post, uidSesion, nombreSesion, texto, respondeAUid)
            },
            alAbrirPerfil = { abrirPerfil(it) },
            alEliminar = {
                Nube.eliminarPublicacion(it.id)
                if (postVista?.id == it.id) {
                    postVista = null
                    pestana = "notificaciones"
                }
            },
            escucharComentarios = { postId, alCambiar -> Nube.escucharComentarios(postId, alCambiar) }
        )
    }

    // Al tocar una notificación se marca como leída y:
    //  - "me gusta": muestra la publicación
    //  - comentario / respuesta: muestra la publicación con los comentarios abiertos en el comentario exacto
    //  - nuevo seguidor: abre el perfil de esa persona
    fun abrirNotificacion(noti: NotiUi) {
        if (noti.nueva) Nube.marcarLeidas(listOf(noti.id))
        if (noti.tipo == "seguidor" || noti.postId.isBlank()) {
            abrirPerfil(noti.deUid)
            return
        }
        fun mostrar(post: PostUi) {
            postVista = post
            pestana = "publicacion"
            if (noti.tipo == "comentario" || noti.tipo == "respuesta") {
                comentarioDeNotificacion = noti.comentarioId
                postDeNotificacion = post
            }
        }
        val enFeed = publicaciones.firstOrNull { it.id == noti.postId }
        if (enFeed != null) {
            mostrar(enFeed)
        } else {
            Nube.obtenerPublicacion(noti.postId, uidSesion) { post ->
                if (post != null) {
                    mostrar(post)
                } else {
                    Toast.makeText(context, "Esa publicación ya no existe.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    CompositionLocalProvider(LocalAcciones provides acciones, LocalFotos provides fotos) {
    when (ruta) {
        "login" -> PantallaLogin(
            correoInicial = "",
            mensaje = mensaje,
            mensajeEsError = mensajeEsError,
            ocupado = ocupado,
            alIniciarSesion = { correo, contrasena ->
                if (correo.isBlank() || contrasena.isBlank()) {
                    mensaje = "Completa tu correo y contraseña."
                    mensajeEsError = true
                } else {
                    ocupado = true
                    mensaje = ""
                    Nube.iniciarSesion(correo, contrasena) { error ->
                        // Si salió bien, la pantalla cambia sola al detectar la sesión
                        if (error != null) {
                            ocupado = false
                            mensaje = error
                            mensajeEsError = true
                        }
                    }
                }
            },
            alIrARegistro = {
                mensaje = ""
                ruta = "registro"
            }
        )

        "registro" -> PantallaRegistro(
            mensaje = if (mensajeEsError) mensaje else "",
            ocupado = ocupado,
            alCrearCuenta = { nombre, correo, contrasena, nacimiento, acepta ->
                when {
                    nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || nacimiento.isBlank() -> {
                        mensaje = "Completa todos los campos."
                        mensajeEsError = true
                    }
                    !correo.contains("@") -> {
                        mensaje = "Escribe un correo válido."
                        mensajeEsError = true
                    }
                    contrasena.length < 6 -> {
                        mensaje = "La contraseña debe tener al menos 6 caracteres."
                        mensajeEsError = true
                    }
                    !acepta -> {
                        mensaje = "Debes aceptar los términos y condiciones."
                        mensajeEsError = true
                    }
                    else -> {
                        ocupado = true
                        mensaje = ""
                        Nube.registrar(nombre, correo, contrasena, nacimiento) { error ->
                            // Si salió bien, entra solo a la app al detectar la sesión
                            if (error != null) {
                                ocupado = false
                                mensaje = error
                                mensajeEsError = true
                            }
                        }
                    }
                }
            },
            alIrALogin = {
                mensaje = ""
                ruta = "login"
            }
        )

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SC.Fondo)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .nestedScroll(conexionScroll)
                        .pointerInput(pestana) {
                            // Deslizar a la derecha: pestaña anterior. A la izquierda: la siguiente.
                            var recorrido = 0f
                            val umbral = 80.dp.toPx()
                            detectHorizontalDragGestures(
                                onDragStart = { recorrido = 0f },
                                onDragEnd = {
                                    val indice = ORDEN_PESTANAS.indexOf(pestana)
                                    if (indice >= 0) {
                                        if (recorrido > umbral && indice > 0) {
                                            irAPestana(ORDEN_PESTANAS[indice - 1])
                                        } else if (recorrido < -umbral && indice < ORDEN_PESTANAS.lastIndex) {
                                            irAPestana(ORDEN_PESTANAS[indice + 1])
                                        }
                                    }
                                    recorrido = 0f
                                },
                                onDragCancel = { recorrido = 0f },
                                onHorizontalDrag = { _, cantidad -> recorrido += cantidad }
                            )
                        }
                ) {
                    when (pestana) {
                        "notificaciones" -> PantallaNotificacionesSC(
                            notificaciones = notificaciones,
                            activadas = notificacionesActivadas,
                            alMarcarLeidas = {
                                Nube.marcarLeidas(notificaciones.filter { it.nueva }.map { it.id })
                            },
                            alAbrir = { abrirNotificacion(it) }
                        )
                        "videos" -> PantallaVideos(
                            videos = publicaciones.filter { it.mediaTipo == "video" },
                            alAbrirNotificaciones = { pestana = "notificaciones" },
                            notificacionesPendientes = pendientes
                        )
                        "crear" -> PantallaCrear(
                            nombre = nombreSesion,
                            usuario = handle,
                            uid = uidSesion,
                            alPublicar = { texto, medio, alTerminar ->
                                if (medio == null) {
                                    Nube.publicar(uidSesion, nombreSesion, handle, texto)
                                    pestana = "inicio"
                                    alTerminar(null)
                                } else {
                                    // Primero se sube el archivo a Cloudinary y luego se guarda la publicación
                                    Medios.subir(context, medio.uri, medio.esVideo) { url, error ->
                                        if (url != null) {
                                            Nube.publicar(
                                                uidSesion, nombreSesion, handle, texto,
                                                url, if (medio.esVideo) "video" else "imagen"
                                            )
                                            pestana = "inicio"
                                            alTerminar(null)
                                        } else {
                                            alTerminar(error ?: "No se pudo subir el archivo.")
                                        }
                                    }
                                }
                            },
                            alCancelar = { pestana = "inicio" }
                        )
                        "perfilAjeno" -> PantallaPerfilAjeno(
                            usuario = usuarios.firstOrNull { it.uid == verPerfilUid },
                            publicaciones = publicaciones.filter { it.autorUid == verPerfilUid },
                            seguidores = usuarios.count { verPerfilUid in it.siguiendo },
                            siguiendo = usuarios.firstOrNull { it.uid == verPerfilUid }?.siguiendo?.size ?: 0,
                            sigue = verPerfilUid in miSiguiendo,
                            alSeguir = {
                                Nube.alternarSeguir(uidSesion, nombreSesion, verPerfilUid, verPerfilUid in miSiguiendo)
                            },
                            alAbrirSeguidores = { abrirLista(verPerfilUid, "seguidores") },
                            alAbrirSiguiendo = { abrirLista(verPerfilUid, "siguiendo") },
                            alVolver = { pestana = if (abiertoDesdeLista) "lista" else origenPerfil }
                        )
                        "publicacion" -> PantallaPublicacion(
                            post = postVista,
                            alVolver = { pestana = "notificaciones" }
                        )
                        "lista" -> {
                            val dueno = usuarios.firstOrNull { it.uid == listaDeUid }
                            val esMia = listaDeUid == uidSesion
                            val cuentas = if (listaTipo == "seguidores") {
                                usuarios.filter { listaDeUid in it.siguiendo }
                            } else {
                                val ids = dueno?.siguiendo ?: emptyList()
                                usuarios.filter { it.uid in ids }
                            }
                            PantallaListaPersonas(
                                titulo = if (listaTipo == "seguidores") "Seguidores" else "Siguiendo",
                                personas = cuentas.map {
                                    PersonaUi(
                                        nombre = it.nombre.ifBlank { it.usuario },
                                        usuario = "@${it.usuario}",
                                        detalle = it.bio.ifBlank { "Miembro de la comunidad" },
                                        uid = it.uid
                                    )
                                },
                                siguiendo = miSiguiendo.toSet(),
                                miUid = uidSesion,
                                vacio = if (listaTipo == "seguidores") {
                                    if (esMia) "Todavía nadie te sigue." else "Todavía nadie sigue a esta cuenta."
                                } else {
                                    if (esMia) "Todavía no sigues a nadie. Busca personas en «Buscar»." else "Esta cuenta todavía no sigue a nadie."
                                },
                                alAbrirPerfil = { abrirPerfil(it.uid) },
                                alAlternarSeguir = { persona ->
                                    Nube.alternarSeguir(uidSesion, nombreSesion, persona.uid, persona.uid in miSiguiendo)
                                },
                                alVolver = { volverDeLista() }
                            )
                        }
                        "buscar" -> PantallaBuscar(
                            personas = personas,
                            siguiendo = miSiguiendo.toSet(),
                            publicaciones = publicaciones,
                            recientes = recientes,
                            alAbrirPerfil = { abrirPerfil(it.uid) },
                            alAlternarSeguir = { persona ->
                                Nube.alternarSeguir(uidSesion, nombreSesion, persona.uid, persona.uid in miSiguiendo)
                            },
                            alGuardarBusqueda = { texto ->
                                val limpio = texto.replace("|", " ").trim()
                                guardarRecientes(
                                    listOf(limpio) + recientes.filter { !it.equals(limpio, ignoreCase = true) }.take(4)
                                )
                            },
                            alQuitarBusqueda = { guardarRecientes(recientes - it) },
                            alBorrarBusquedas = { guardarRecientes(emptyList()) },
                            alAbrirNotificaciones = { pestana = "notificaciones" },
                            notificacionesPendientes = pendientes
                        )
                        "perfil" -> PantallaPerfil(
                            nombre = nombreSesion,
                            usuario = handle,
                            uid = uidSesion,
                            bio = bioSesion,
                            seguidores = seguidores,
                            siguiendo = miSiguiendo.size,
                            misPublicaciones = publicaciones.filter { it.autorUid == uidSesion },
                            alAbrirConfiguracion = { pestana = "configuracion" },
                            alAbrirSeguidores = { abrirLista(uidSesion, "seguidores") },
                            alAbrirSiguiendo = { abrirLista(uidSesion, "siguiendo") }
                        )
                        "configuracion" -> PantallaConfiguracion(
                            uid = uidSesion,
                            nombreInicial = nombreSesion,
                            bioInicial = bioSesion,
                            correo = correoSesion,
                            notificacionesInicial = perfil?.notificaciones ?: true,
                            privadaInicial = perfil?.privada ?: false,
                            modoTema = modoTema,
                            alCambiarTema = {
                                modoTema = it
                                preferencias.edit().putString("tema_app", it).apply()
                            },
                            alCambiarFoto = { uri, alTerminar ->
                                Medios.subir(context, uri, false) { url, error ->
                                    if (url != null) {
                                        Nube.guardarFoto(uidSesion, url)
                                        alTerminar(null)
                                    } else {
                                        alTerminar(error ?: "No se pudo subir la foto.")
                                    }
                                }
                            },
                            alGuardarPerfil = { nuevoNombre, nuevaBio ->
                                Nube.guardarPerfil(uidSesion, nuevoNombre, nuevaBio)
                            },
                            alCambiarContrasena = { actual, nueva, alTerminar ->
                                if (nueva.length < 6) {
                                    alTerminar("La nueva contraseña debe tener al menos 6 caracteres.")
                                } else {
                                    Nube.cambiarContrasena(actual, nueva, alTerminar)
                                }
                            },
                            alCambiarNotificaciones = {
                                Nube.guardarAjuste(uidSesion, "notificaciones", it)
                            },
                            alCambiarPrivada = {
                                Nube.guardarAjuste(uidSesion, "privada", it)
                            },
                            alCerrarSesion = { Nube.cerrarSesion() },
                            alVolver = { pestana = "perfil" }
                        )
                        else -> PantallaFeed(
                            publicaciones = publicaciones,
                            siguiendo = miSiguiendo.toSet(),
                            alAbrirNotificaciones = { pestana = "notificaciones" },
                            notificacionesPendientes = pendientes
                        )
                    }
                }
                postDeNotificacion?.let { post ->
                    HojaComentarios(post, resaltarId = comentarioDeNotificacion) {
                        postDeNotificacion = null
                        comentarioDeNotificacion = ""
                    }
                }
                AnimatedVisibility(visible = barraVisible) {
                    BarraNavegacion(
                        actual = when (pestana) {
                            "notificaciones", "publicacion" -> "inicio"
                            "configuracion", "lista" -> "perfil"
                            "perfilAjeno" -> when {
                                abiertoDesdeLista -> "perfil"
                                origenPerfil == "notificaciones" -> "inicio"
                                else -> origenPerfil
                            }
                            else -> pestana
                        },
                        alIr = { irAPestana(it) }
                    )
                }
            }
        }
    }
    }
}
