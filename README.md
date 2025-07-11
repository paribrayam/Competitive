# GeoQuiz - Aplicación de Preguntas y Respuestas

## Descripción
GeoQuiz es una aplicación Android de preguntas y respuestas con múltiples funcionalidades avanzadas, incluyendo juegos multijugador, sistema de ranking, colección de geodas y herramientas educativas.

## Características Principales

### 🎮 Juegos
- **Juego Online**: Multijugador en tiempo real con otros jugadores
- **Juego Bluetooth**: Multijugador local usando conexión Bluetooth
- **Sistema de Ranking**: 10 niveles de progresión basados en victorias y geodas
- **Historial de Juegos**: Seguimiento de partidas jugadas

### 💎 Sistema de Geodas
- **4 Tipos de Geodas**: Bronce, Oro, Diamante y Galáctica
- **Probabilidades de Recompensa**: Sistema de recompensas basado en probabilidades
- **Galería de Geodas**: Visualización de geodas recolectadas
- **Progresión de Rango**: Las geodas contribuyen al avance de nivel

### 🎵 Música y Audio
- **Música de Fondo**: Reproducción continua durante el juego
- **Control de Volumen**: Ajuste de volumen integrado
- **Configuración de Audio**: Habilitar/deshabilitar música

### 🛠️ Herramientas Educativas
- **Calculadora**: Calculadora científica integrada
- **Calendario**: Gestión de eventos y recordatorios
- **Traductor**: Traducción de texto en tiempo real
- **Navegador Web**: Navegación web integrada
- **Dibujo**: Herramienta de dibujo libre
- **Pixel Art**: Editor de arte pixelado

### 📱 Interfaz de Usuario
- **Navegación Inferior**: Home, Dashboard y Notificaciones
- **Pantalla de Rangos**: Visualización de todos los niveles disponibles
- **Pantalla de Ajustes**: Configuración de música y preferencias
- **Diseño Moderno**: UI/UX optimizada para Android

## Tecnologías Utilizadas

### Frontend
- **Android Studio**: IDE principal
- **Java**: Lenguaje de programación
- **XML**: Layouts y recursos
- **Material Design**: Componentes de UI

### Backend y Servicios
- **Firebase**: Base de datos en tiempo real para multijugador
- **Bluetooth API**: Conexión local entre dispositivos
- **SharedPreferences**: Persistencia de datos offline
- **MediaPlayer**: Reproducción de música de fondo

### APIs y Servicios Externos
- **Google Translate API**: Traducción de texto
- **WebView**: Navegación web integrada

## Instalación y Configuración

### Prerrequisitos
- Android Studio (versión 4.0 o superior)
- JDK 8 o superior
- Dispositivo Android (API 21+) o emulador
- Conexión a Internet (para funcionalidades online)

### Pasos de Instalación
1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/paribrayam/Competitive.git
   cd Competitive
   ```

2. **Abrir en Android Studio**
   - Abrir Android Studio
   - Seleccionar "Open an existing project"
   - Navegar a la carpeta del proyecto y seleccionarla

3. **Configurar Firebase** (opcional para multijugador)
   - Crear proyecto en Firebase Console
   - Descargar `google-services.json`
   - Colocar en la carpeta `app/`

4. **Sincronizar Gradle**
   - Android Studio sincronizará automáticamente las dependencias
   - Si no, hacer clic en "Sync Now"

5. **Ejecutar la aplicación**
   - Conectar dispositivo Android o iniciar emulador
   - Presionar "Run" (▶️) en Android Studio

## Estructura del Proyecto

```
MyApplication/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/myapplication/
│   │   │   ├── Activities/          # Actividades principales
│   │   │   ├── Fragments/           # Fragmentos de navegación
│   │   │   ├── Adapters/            # Adaptadores para listas
│   │   │   └── Utils/               # Clases utilitarias
│   │   ├── res/
│   │   │   ├── layout/              # Layouts XML
│   │   │   ├── drawable/            # Imágenes y recursos
│   │   │   ├── values/              # Strings, colores, temas
│   │   │   └── raw/                 # Archivos de audio
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
└── README.md
```

## Funcionalidades Detalladas

### Sistema de Ranking
- **10 Niveles**: Desde principiante hasta experto
- **Progresión**: Basada en victorias y geodas recolectadas
- **Recompensas**: Desbloqueo de nuevas funcionalidades por nivel

### Gestión de Datos
- **Persistencia Offline**: Datos guardados localmente
- **Sincronización**: Datos sincronizados cuando hay conexión
- **Backup**: Sistema de respaldo automático

### Configuración
- **Ajustes de Audio**: Control de música y efectos
- **Preferencias de Juego**: Configuración personalizada
- **Privacidad**: Política de privacidad integrada

## Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Contacto

- **Desarrollador**: Brayam Pari
- **Email**: [paribrayam71@gmail.com]
- **GitHub**: [https://github.com/paribrayam](https://github.com/paribrayam)

## Changelog

### v1.0.0
- Lanzamiento inicial
- Sistema de preguntas y respuestas
- Multijugador online y Bluetooth
- Sistema de ranking y geodas
- Herramientas educativas integradas
- Música de fondo y configuración de audio 
