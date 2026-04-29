package ni.edu.uam.flappy_raccoon

/**
 * Constantes globales que definen la lógica y apariencia del juego.
 */
object GameConstants {
    // --- Configuración del Mapache ---
    
    /** Tamaño visual del mapache (en píxeles) */
    const val RACCOON_SIZE = 360f 
    
    /** 
     * El hitbox es más pequeño que la imagen para que no se sienta injusto al chocar.
     * Define el ancho real de colisión.
     */
    const val RACCOON_HITBOX_WIDTH = 170f 
    
    /** Define el alto real de colisión del mapache */
    const val RACCOON_HITBOX_HEIGHT = 140f
    
    // --- Configuración de los Obstáculos (Tubos) ---
    
    /** Ancho del cuerpo principal del tubo */
    const val PIPE_WIDTH = 240f 
    
    /** Ancho de la parte superior (tapa) del tubo */
    const val PIPE_CAP_WIDTH = 280f 
    
    /** Altura de la parte superior (tapa) del tubo */
    const val PIPE_CAP_HEIGHT = 120f 

    /** Ancho del área de colisión de los tubos */
    const val PIPE_HITBOX_WIDTH = 180f 
    
    // --- Física y Dinámica del Juego ---
    
    /** Fuerza de gravedad aplicada al mapache en cada frame */
    const val GRAVITY = 0.8f 
    
    /** Impulso ascendente que se aplica al saltar (valor negativo para subir) */
    const val JUMP_VELOCITY = -16f 
    
    /** Espacio vertical libre entre el tubo superior y el inferior */
    const val PIPE_GAP = 700f 
    
    /** Velocidad a la que se desplazan los tubos hacia la izquierda */
    const val PIPE_SPEED = 10f
    
    /** Distancia horizontal que separa a un par de tubos del siguiente */
    const val PIPE_DISTANCE = 1100f
}
