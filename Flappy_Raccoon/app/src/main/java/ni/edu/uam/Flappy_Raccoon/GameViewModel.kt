package ni.edu.uam.flappy_raccoon

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

data class Skin(val imageRes: Int, val name: String, val gameOverRes: Int = R.drawable.gameover)

enum class Difficulty { PRACTICE, NORMAL, HARDCORE }

@SuppressLint("AutoboxingStateCreation")
class GameViewModel : ViewModel() {
    var raccoonY by mutableFloatStateOf(500f)
    var raccoonVelocity by mutableFloatStateOf(0f)
    var score by mutableIntStateOf(0)
    var isGameOver by mutableStateOf(false)
    var gameStarted by mutableStateOf(false)
    var isPaused by mutableStateOf(false)
    var countdown by mutableIntStateOf(0)
    
    var showSkinSelection by mutableStateOf(false)
    var currentSkinIndex by mutableIntStateOf(0)
    
    // --- ESTADOS DE OPCIONES ---
    var difficulty by mutableStateOf(Difficulty.NORMAL)
    var isNightMode by mutableStateOf(false)
    var showClouds by mutableStateOf(true)

    val skins = listOf(
        Skin(R.drawable.raccoon, "Raccoon"),
        Skin(R.drawable.racc_skin2, "Orange Raccoon", gameOverRes = R.drawable.orangeracc_go),
        Skin(R.drawable.racc_skin3, "Saiyan Raccoon", gameOverRes = R.drawable.saiyanracc_go),
        Skin(R.drawable.racc_skin4, "Coco", gameOverRes = R.drawable.coco_go),
        Skin(R.drawable.racc_skin5, "Cule Raccoon", gameOverRes = R.drawable.culeracc_go),
        Skin(R.drawable.racc_skin6, "Bruce", gameOverRes = R.drawable.bruce_go),
        Skin(R.drawable.racc_skin7, "UAM Raccoon", gameOverRes = R.drawable.uamracc_go)
    )

    val pipes = mutableStateListOf<Pipe>()

    private var screenWidth = 0f
    private var screenHeight = 0f
    private var gameJob: Job? = null

    private fun getCurrentSpeed(): Float {
        // En modo práctica la velocidad es reducida y no aumenta
        if (difficulty == Difficulty.PRACTICE) return GameConstants.PIPE_SPEED * 0.7f
        
        // En modo Pro, empezamos con un multiplicador mayor
        val multiplier = if (difficulty == Difficulty.HARDCORE) 1.5f else 1.0f
        val baseSpeed = GameConstants.PIPE_SPEED * multiplier
        
        return when {
            score >= 1000 -> baseSpeed * 2.5f
            score >= 500  -> baseSpeed * 2.2f
            score >= 200  -> baseSpeed * 2.0f
            score >= 100  -> baseSpeed * 1.8f
            score >= 50   -> baseSpeed * 1.6f
            score >= 30   -> baseSpeed * 1.4f
            score >= 10   -> baseSpeed * 1.2f
            else          -> baseSpeed
        }
    }

    private fun getCurrentGap(): Float {
        if (difficulty == Difficulty.PRACTICE) return GameConstants.PIPE_GAP * 1.2f
        
        val baseGap = GameConstants.PIPE_GAP
        val extraReduction = if (difficulty == Difficulty.HARDCORE) 100f else 0f
        
        val reduction = when {
            score >= 200 -> 200f
            score >= 100 -> 150f
            score >= 50  -> 100f
            score >= 30  -> 60f
            score >= 10  -> 30f
            else         -> 0f
        }
        return (baseGap - reduction - extraReduction).coerceAtLeast(500f)
    }

    private fun getCurrentPipeDistance(): Float {
        val baseDistance = GameConstants.PIPE_DISTANCE
        val reduction = when {
            score >= 200 -> 300f
            score >= 100 -> 200f
            score >= 50  -> 150f
            score >= 30  -> 100f
            score >= 10  -> 50f
            else         -> 0f
        }
        return (baseDistance - reduction).coerceAtLeast(750f)
    }

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
        
        viewModelScope.launch {
            gameStarted = true
            isGameOver = false
            isPaused = false
            showSkinSelection = false
            
            for (i in 3 downTo 1) {
                countdown = i
                delay(1000)
            }
            countdown = 0
            
            gameLoop()
        }
    }

    fun jump() {
        if (!gameStarted) {
            startGame()
        }
        if (!isGameOver && !isPaused && countdown == 0) {
            raccoonVelocity = GameConstants.JUMP_VELOCITY
        }
    }

    private fun resetGame() {
        raccoonY = screenHeight / 2
        raccoonVelocity = 0f
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
        showSkinSelection = false
        resetGame()
    }

    private fun spawnPipe() {
        val currentGap = getCurrentGap()
        val minHeight = 150f
        val maxHeight = (screenHeight - currentGap - 150f).coerceAtLeast(minHeight)
        
        val lastHeight = pipes.lastOrNull()?.topHeight ?: (screenHeight / 2)
        val maxDiff = if (score >= 50) 450f else 350f 
        
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
                delay(16)
            }
        }
    }

    private fun updatePhysics() {
        raccoonVelocity += GameConstants.GRAVITY
        raccoonY += raccoonVelocity

        val currentSpeed = getCurrentSpeed()
        val iterator = pipes.listIterator()
        while (iterator.hasNext()) {
            val pipe = iterator.next()
            pipe.x -= currentSpeed 

            if (!pipe.passed && pipe.x + GameConstants.PIPE_WIDTH < screenWidth / 4) {
                pipe.passed = true
                score++
            }
        }

        if (pipes.isNotEmpty() && pipes[0].x + GameConstants.PIPE_WIDTH < 0) {
            pipes.removeAt(0)
        }

        if (pipes.isNotEmpty() && pipes.last().x < screenWidth - getCurrentPipeDistance()) {
            spawnPipe()
        }
    }

    private fun checkCollisions() {
        if (raccoonY - GameConstants.RACCOON_HITBOX_HEIGHT/2 <= 0 || 
            raccoonY + GameConstants.RACCOON_HITBOX_HEIGHT/2 >= screenHeight) {
            isGameOver = true
            return
        }

        val raccoonX = screenWidth / 4
        val bLeft = raccoonX - GameConstants.RACCOON_HITBOX_WIDTH / 2
        val bRight = raccoonX + GameConstants.RACCOON_HITBOX_WIDTH / 2
        val bTop = raccoonY - GameConstants.RACCOON_HITBOX_HEIGHT / 2
        val bBottom = raccoonY + GameConstants.RACCOON_HITBOX_HEIGHT / 2

        for (pipe in pipes) {
            val pLeft = pipe.x + (GameConstants.PIPE_WIDTH - GameConstants.PIPE_HITBOX_WIDTH) / 2
            val pRight = pLeft + GameConstants.PIPE_HITBOX_WIDTH
            
            if (bRight > pLeft && bLeft < pRight) {
                val currentGap = getCurrentGap()
                if (bTop < pipe.topHeight || bBottom > pipe.topHeight + currentGap) {
                    isGameOver = true
                    return
                }
            }
        }
    }
    
    fun nextSkin() {
        currentSkinIndex = (currentSkinIndex + 1) % skins.size
    }
    
    fun prevSkin() {
        currentSkinIndex = if (currentSkinIndex <= 0) skins.size - 1 else currentSkinIndex - 1
    }
}
