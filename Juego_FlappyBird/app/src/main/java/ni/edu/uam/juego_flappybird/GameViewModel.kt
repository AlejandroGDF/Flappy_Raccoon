package ni.edu.uam.juego_flappybird

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Pipe(
    var x: Float,
    val topHeight: Float,
    var passed: Boolean = false
)

class GameViewModel : ViewModel() {
    var birdY by mutableStateOf(500f)
    var birdVelocity by mutableStateOf(0f)
    var score by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)
    var gameStarted by mutableStateOf(false)
    var isPaused by mutableStateOf(false)
    var countdown by mutableIntStateOf(0)

    val pipes = mutableStateListOf<Pipe>()

    private var screenWidth = 0f
    private var screenHeight = 0f
    private var gameJob: Job? = null

    fun onSizeChanged(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
        if (!gameStarted && !isGameOver) {
            resetGame()
        }
    }

    fun startGame() {
        if (isGameOver) {
            resetGame()
        }
        gameStarted = true
        isGameOver = false
        isPaused = false
        gameLoop()
    }

    fun jump() {
        if (!gameStarted) {
            startGame()
        }
        if (!isGameOver && !isPaused && countdown == 0) {
            birdVelocity = GameConstants.JUMP_VELOCITY
        }
    }

    private fun resetGame() {
        birdY = screenHeight / 2
        birdVelocity = 0f
        score = 0
        isGameOver = false
        isPaused = false
        countdown = 0
        pipes.clear()
        spawnPipe()
    }

    fun pauseGame() {
        if (gameStarted && !isGameOver) {
            isPaused = true
        }
    }

    fun resumeGame() {
        viewModelScope.launch {
            isPaused = false
            for (i in 3 downTo 1) {
                countdown = i
                delay(1000)
            }
            countdown = 0
        }
    }

    fun goToMainMenu() {
        gameStarted = false
        isGameOver = false
        isPaused = false
        countdown = 0
        resetGame()
    }

    private fun spawnPipe() {
        val minHeight = 150f
        val maxHeight = (screenHeight - GameConstants.PIPE_GAP - 150f).coerceAtLeast(minHeight)
        
        val lastHeight = pipes.lastOrNull()?.topHeight ?: (screenHeight / 2)
        val maxDiff = 350f 
        
        val low = (lastHeight - maxDiff).coerceAtLeast(minHeight)
        val high = (lastHeight + maxDiff).coerceAtMost(maxHeight)
        
        val topHeight = if (high > low) Random.nextFloat() * (high - low) + low else low
        pipes.add(Pipe(screenWidth, topHeight))
    }

    private fun gameLoop() {
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            while (gameStarted && !isGameOver) {
                if (!isPaused && countdown == 0) {
                    updatePhysics()
                    checkCollisions()
                }
                delay(16) // ~60 FPS
            }
        }
    }

    private fun updatePhysics() {
        // Bird physics
        birdVelocity += GameConstants.GRAVITY
        birdY += birdVelocity

        // Pipes physics
        val iterator = pipes.listIterator()
        while (iterator.hasNext()) {
            val pipe = iterator.next()
            pipe.x -= GameConstants.PIPE_SPEED

            if (!pipe.passed && pipe.x + GameConstants.PIPE_WIDTH < screenWidth / 4) {
                pipe.passed = true
                score++
            }
        }

        // Remove off-screen pipes
        if (pipes.isNotEmpty() && pipes[0].x + GameConstants.PIPE_WIDTH < 0) {
            pipes.removeAt(0)
        }

        // Spawn new pipe
        if (pipes.isNotEmpty() && pipes.last().x < screenWidth - GameConstants.PIPE_DISTANCE) {
            spawnPipe()
        }
    }

    private fun checkCollisions() {
        // Floor and Ceiling - Usamos una altura de hitbox reducida
        if (birdY - GameConstants.BIRD_HITBOX_HEIGHT/2 <= 0 || 
            birdY + GameConstants.BIRD_HITBOX_HEIGHT/2 >= screenHeight) {
            isGameOver = true
            return
        }

        // Centro visual del pájaro
        val birdX = screenWidth / 4
        
        // Definimos los bordes de la hitbox del pájaro (centrada en su posición visual)
        val bLeft = birdX - GameConstants.BIRD_HITBOX_WIDTH / 2
        val bRight = birdX + GameConstants.BIRD_HITBOX_WIDTH / 2
        val bTop = birdY - GameConstants.BIRD_HITBOX_HEIGHT / 2
        val bBottom = birdY + GameConstants.BIRD_HITBOX_HEIGHT / 2

        for (pipe in pipes) {
            // Hitbox de la papelera (centrada en su ancho visual)
            val pLeft = pipe.x + (GameConstants.PIPE_WIDTH - GameConstants.PIPE_HITBOX_WIDTH) / 2
            val pRight = pLeft + GameConstants.PIPE_HITBOX_WIDTH
            
            // Verificamos si hay colisión en el eje X
            if (bRight > pLeft && bLeft < pRight) {
                // Colisión con el tubo superior o inferior
                if (bTop < pipe.topHeight || bBottom > pipe.topHeight + GameConstants.PIPE_GAP) {
                    isGameOver = true
                    return
                }
            }
        }
    }
}
