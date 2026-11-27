# Social Community

Aplicación de red social para Android: publica texto, fotos y videos, sigue a otras personas, comenta y recibe notificaciones. Hecha con **Kotlin** y **Jetpack Compose**.

> Estado: **Alpha** (en desarrollo).

<!-- Agrega aquí capturas de pantalla, por ejemplo: ![Inicio](docs/inicio.png) -->

## Funciones

**Cuenta**
- Registro con nombre, correo, contraseña, fecha de nacimiento y aceptación de términos.
- Inicio de sesión. Quien abre la app por primera vez ve primero el registro; quien ya tuvo sesión, el inicio de sesión.

**Publicaciones**
- Crear publicaciones con texto, foto o video.
- Me gusta, comentarios y compartir. Borrar y copiar el texto de tus propias publicaciones.
- Responder a un comentario: la persona recibe una notificación.
- Vista previa de fotos a pantalla completa con zoom (pellizcar, arrastrar y doble toque).
- Reproductor de video con controles que se ocultan solos.

**Personas**
- Buscar cuentas, con historial de búsquedas recientes.
- Seguir y dejar de seguir. Listas de **seguidores** y **siguiendo**, en tu perfil y en el de otras cuentas.
- Perfil con foto, biografía, estadísticas y publicaciones.

**Notificaciones**
- Avisos de me gusta, comentarios, respuestas y nuevos seguidores.
- Al tocar una notificación se abre la publicación (con el comentario resaltado) o el perfil de quien te siguió.

**Experiencia de uso**
- Tema claro, oscuro o el del sistema.
- Deslizar de lado para pasar entre Inicio, Videos, Buscar y Perfil.
- La barra inferior se oculta al bajar por una lista y vuelve al subir.
- Pantalla de arranque con el logo de la app.

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje e interfaz | Kotlin 2.2, Jetpack Compose (Material 3) |
| Cuentas | Firebase Authentication (correo y contraseña) |
| Base de datos | Cloud Firestore |
| Fotos y videos | Cloudinary (subida sin firma con OkHttp) |
| Versión mínima de Android | 7.0 (API 24) |

## Cómo ejecutarla

Necesitas una versión reciente de **Android Studio**, una cuenta de **Firebase** y una de **Cloudinary** (ambas tienen plan gratuito).

### 1. Clonar el proyecto

```bash
git clone https://github.com/ArmandoRiosGuajardo55/SocialComunnity.git
```

Ábrelo con Android Studio y espera a que Gradle sincronice.

### 2. Configurar Firebase

1. Crea un proyecto en la [consola de Firebase](https://console.firebase.google.com).
2. Agrega una app de Android con el nombre de paquete `com.example.socialcomunnity`.
3. Descarga el archivo `google-services.json` y colócalo en la carpeta `app/`.
4. En **Authentication → Método de acceso**, activa **Correo electrónico/contraseña**.
5. En **Firestore Database**, crea la base de datos y pega el contenido de `firestore.rules` en la pestaña **Reglas**.

### 3. Configurar Cloudinary

1. Crea una cuenta en [Cloudinary](https://cloudinary.com) y copia tu **Cloud name** desde el panel.
2. En **Settings → Upload → Upload presets**, crea un preset de tipo **Unsigned**.
3. Abre `app/src/main/java/com/example/socialcomunnity/nube/ConfigCloudinary.kt` y cambia los dos valores:

```kotlin
const val NOMBRE_NUBE = "tu-cloud-name"
const val PRESET = "tu-upload-preset"
```

### 4. Ejecutar

Conecta un teléfono o abre un emulador y pulsa **Run**.

## Generar un APK

En Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)**. El archivo queda en `app/build/outputs/apk/debug/`.

## Autor

[ArmandoRiosGuajardo55](https://github.com/ArmandoRiosGuajardo55)
