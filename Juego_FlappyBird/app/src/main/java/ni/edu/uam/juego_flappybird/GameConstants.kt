package ni.edu.uam.juego_flappybird

object GameConstants {
    const val BIRD_SIZE = 360f // Mapache aún más grande
    
    // Hitbox ajustada para el nuevo tamaño
    const val BIRD_HITBOX_WIDTH = 170f 
    const val BIRD_HITBOX_HEIGHT = 140f
    
    const val PIPE_WIDTH = 240f // Ancho del cuerpo de la papelera
    const val PIPE_CAP_WIDTH = 280f // Tapa con ancho original
    const val PIPE_CAP_HEIGHT = 120f // Tapa mucho más alta

    const val PIPE_HITBOX_WIDTH = 180f 
    
    // Velocidades aumentadas para que el juego sea más rápido y fluido
    const val GRAVITY = 0.8f // Antes 0.5f
    const val JUMP_VELOCITY = -16f // Antes -14f
    const val PIPE_GAP = 700f 
    const val PIPE_SPEED = 10f // Antes 7f
    const val PIPE_DISTANCE = 1100f
}
