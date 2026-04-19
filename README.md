🦝 Flappy Raccoon
Flappy Raccoon es un emocionante juego tipo endless runner desarrollado nativamente para Android utilizando Jetpack Compose. Inspirado en el clásico Flappy Bird, este proyecto lleva la experiencia a otro nivel con un sistema de dificultad progresiva, múltiples skins desbloqueables y una estética visual vibrante.
Versión Kotlin Jetpack Compose
✨ Características Principales
🚀 Dificultad Dinámica Progresiva
El juego desafía tus habilidades aumentando la dificultad automáticamente en hitos de puntuación clave (10, 30, 50, 100, 200, 500, 1000 puntos):
•
Velocidad Variable: El ritmo del juego aumenta hasta un 250%.
•
Obstáculos Ajustables: El espacio entre tuberías se reduce y la frecuencia de aparición aumenta.
•
Variación de Altura: Los saltos entre obstáculos se vuelven más erráticos conforme avanzas.
🎭 Sistema de Skins Personalizadas
Selecciona entre una variedad de personajes únicos, cada uno con su propia personalidad:
•
Raccoon Clásico
•
Orange Raccoon
•
Saiyan Raccoon
•
Coco
•
Cule Raccoon
•
Bruce (The Jetpack Dog)
💀 Experiencia Personalizada
•
Pantallas de Muerte Únicas: Cada skin tiene su propia pantalla de Game Over personalizada (como la de Bruce_GO).
•
Menú de Selección Fluido: Interfaz intuitiva para navegar entre tus personajes favoritos.
•
Fondo Animado: Sistema de nubes generadas aleatoriamente con diferentes escalas y velocidades.
🛠️ Stack Tecnológico
•
Lenguaje: Kotlin
•
UI Framework: Jetpack Compose (Declarative UI)
•
Arquitectura: MVVM (Model-View-ViewModel)
•
Gestión de Estado: MutableState, ViewModelScope para el bucle del juego.
•
Gráficos: Canvas API de Compose para renderizado de alto rendimiento.
🚀 Instalación y Uso
1.
Clona el repositorio:
Shell Script
git clone https://github.com/tu-usuario/Flappy_Raccoon.git
2.
Abre el proyecto en Android Studio (Ladybug o superior).
3.
Sincroniza el proyecto con los archivos Gradle.
4.
Ejecuta la aplicación en un emulador o dispositivo físico con Android 7.0 (API 24) o superior.
Nota para usuarios de Xiaomi: Si instalas en un dispositivo físico, asegúrate de activar "Instalar vía USB" en las Opciones de Desarrollador.
🎮 Controles
•
Tocar Pantalla: El mapache realiza un salto para evitar los obstáculos.
•
Botón Pausa: Detiene la acción en cualquier momento.
•
Menú Skins: Cambia tu personaje antes de empezar la partida.
📁 Estructura del Proyecto
•
MainActivity.kt: Contiene toda la interfaz de usuario y el motor de renderizado.
•
GameViewModel.kt: Maneja la lógica de física, detección de colisiones y progresión de dificultad.
•
GameConstants.kt: Almacena los valores base de gravedad, salto y dimensiones de hitboxes.
✒️ Autor
•
Desarrollado por: Alegappy (Fernando)
•
Proyecto: Flappy Raccoon - Desarrollo de Aplicaciones Móviles.
