package ni.edu.uam.flappy_raccoon

object GameConstants {
    // Tamaño visual del mapache
    const val RACCOON_SIZE = 360f 
    
    // El hitbox es más pequeño que la imagen para que no se sienta injusto al chocar
    const val RACCOON_HITBOX_WIDTH = 170f 
    const val RACCOON_HITBOX_HEIGHT = 140f
    
    // Medidas de los tubos
    const val PIPE_WIDTH = 240f 
    const val PIPE_CAP_WIDTH = 280f 
    const val PIPE_CAP_HEIGHT = 120f 

    const val PIPE_HITBOX_WIDTH = 180f 
    
    // Ajustes de la física del juego
    const val GRAVITY = 0.8f 
    const val JUMP_VELOCITY = -16f 
    const val PIPE_GAP = 700f 
    const val PIPE_SPEED = 10f
    const val PIPE_DISTANCE = 1100f
}
