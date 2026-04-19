# 🦝 Flappy Raccoon

**Flappy Raccoon** es un emocionante juego tipo **endless runner** desarrollado nativamente para **Android** utilizando **Jetpack Compose**.

Inspirado en el clásico **Flappy Bird**, este proyecto lleva la experiencia a otro nivel con un sistema de **dificultad progresiva**, **múltiples skins desbloqueables** y una **estética visual vibrante**.

## 📱 Versión

**Kotlin + Jetpack Compose**

---

## ✨ Características principales

### 🚀 Dificultad dinámica progresiva

El juego desafía tus habilidades aumentando la dificultad automáticamente en hitos de puntuación clave:

**10, 30, 50, 100, 200, 500 y 1000 puntos**

Esto incluye:

- **Velocidad variable:** el ritmo del juego aumenta hasta un **250%**.
- **Obstáculos ajustables:** el espacio entre tuberías se reduce y la frecuencia de aparición aumenta.
- **Variación de altura:** los saltos entre obstáculos se vuelven más erráticos conforme avanzas.

### 🎭 Sistema de skins personalizadas

Selecciona entre una variedad de personajes únicos, cada uno con su propia personalidad:

- **Raccoon Clásico**
- **Orange Raccoon**
- **Saiyan Raccoon**
- **Coco**
- **Cule Raccoon**
- **Bruce (The Jetpack Dog)**

### 💀 Experiencia personalizada

- **Pantallas de muerte únicas:** cada skin tiene su propia pantalla de **Game Over** personalizada.
- **Menú de selección fluido:** interfaz intuitiva para navegar entre tus personajes favoritos.
- **Fondo animado:** sistema de nubes generadas aleatoriamente con diferentes escalas y velocidades.

---

## 🛠️ Stack tecnológico

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose
- **Arquitectura:** MVVM (**Model - View - ViewModel**)
- **Gestión de estado:** `MutableState`, `ViewModelScope`
- **Gráficos:** Canvas API de Compose para renderizado de alto rendimiento

---

## 🚀 Instalación y uso

1. **Clona el repositorio:**

```bash
git clone https://github.com/tu-usuario/Flappy_Raccoon.git
```

2. **Abre el proyecto en Android Studio**

Se recomienda usar **Android Studio Ladybug o superior**.

3. **Sincroniza el proyecto con Gradle**

Espera a que Android Studio cargue y sincronice todas las dependencias.

4. **Ejecuta la aplicación**

Puedes correrla en:

- **Emulador Android**
- **Dispositivo físico** con **Android 7.0 (API 24)** o superior

> **Nota para usuarios de Xiaomi:**  
> Si instalas en un dispositivo físico, activa la opción **“Instalar vía USB”** en las **Opciones de desarrollador**.

---

## 🎮 Controles

- **Tocar la pantalla:** el mapache realiza un salto para evitar los obstáculos.
- **Botón de pausa:** detiene la acción en cualquier momento.
- **Menú de skins:** permite cambiar tu personaje antes de empezar la partida.

---

## 📁 Estructura del proyecto

- **MainActivity.kt** → Contiene la interfaz de usuario y el motor de renderizado.
- **GameViewModel.kt** → Maneja la lógica de física, detección de colisiones y progresión de dificultad.
- **GameConstants.kt** → Almacena los valores base de gravedad, salto y dimensiones de hitboxes.

---

## 🎯 Objetivo del proyecto

El objetivo de **Flappy Raccoon** es ofrecer una experiencia divertida y desafiante, combinando mecánicas clásicas de arcade con una implementación moderna en **Jetpack Compose**, aplicando buenas prácticas de desarrollo móvil y arquitectura limpia.

---

## 📌 Estado del proyecto

**En desarrollo** 🚧

---

## ✒️ Autor

- **Desarrollado por:** Alegappy (Alejandro)
- **Proyecto:** Flappy Raccoon - Desarrollo de Aplicaciones Móviles
