package ni.edu.uam.Flappy_Raccoon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ni.edu.uam.Flappy_Raccoon.ui.theme.Juego_FlappyBirdTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Juego_FlappyBirdTheme {
                val viewModel: GameViewModel = viewModel()
                GameScreen(viewModel)
            }
        }
    }
}

data class Cloud(val x: Float, val y: Float, val scale: Float)

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val raccoonPainter = painterResource(id = R.drawable.raccoon)
    val pipeBodyPainter = painterResource(id = R.drawable.pipe_low_texture)
    val pipeCapPainter = painterResource(id = R.drawable.pipe_top_texture)
    val cloudPainter = painterResource(id = R.drawable.cloud)
    val menuLogoPainter = painterResource(id = R.drawable.menutexto)
    val playButtonPainter = painterResource(id = R.drawable.boton_play)
    val optionsButtonPainter = painterResource(id = R.drawable.boton_options)
    val retryButtonPainter = painterResource(id = R.drawable.boton_retry)
    val pauseButtonPainter = painterResource(id = R.drawable.boton_pause)
    val pauseTablePainter = painterResource(id = R.drawable.tabla_frame)
    val homeButtonPainter = painterResource(id = R.drawable.boton_home)
    val resumeButtonPainter = painterResource(id = R.drawable.boton_resume)
    val gameOverPainter = painterResource(id = R.drawable.gameover)
    
    // PNGs para la cuenta regresiva
    val num1Painter = painterResource(id = R.drawable.num1)
    val num2Painter = painterResource(id = R.drawable.num2)
    val num3Painter = painterResource(id = R.drawable.num3)

    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }
    var showOptions by remember { mutableStateOf(false) }

    val clouds = remember { mutableStateListOf<Cloud>() }

    val scoreTextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 64.sp,
        color = Color.White,
        shadow = Shadow(color = Color.Black.copy(alpha = 0.5f), offset = Offset(4f, 4f), blurRadius = 8f)
    )

    // Tamaño estándar para botones interactivos principales
    val mainButtonWidth = 320.dp
    val mainButtonHeight = 110.dp

    LaunchedEffect(screenWidth, screenHeight) {
        if (screenWidth > 0 && clouds.isEmpty()) {
            val rows = 3; val cols = 2
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    clouds.add(Cloud(
                        x = (c * (screenWidth / cols)) + Random.nextFloat() * (screenWidth / cols * 0.5f),
                        y = (r * (screenHeight * 0.5f / rows)) + Random.nextFloat() * 100f,
                        scale = 1.5f + Random.nextFloat() * 1.5f 
                    ))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF70C5CE))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { 
                if (viewModel.gameStarted && !viewModel.isGameOver && !viewModel.isPaused && viewModel.countdown == 0) viewModel.jump() 
            }
            .onGloballyPositioned { coordinates ->
                screenWidth = coordinates.size.width.toFloat()
                screenHeight = coordinates.size.height.toFloat()
                viewModel.onSizeChanged(screenWidth, screenHeight)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Nubes
            clouds.forEach { cloud ->
                val cloudWidth = 300f * cloud.scale
                val cloudHeight = 180f * cloud.scale
                translate(cloud.x - cloudWidth/2, cloud.y) {
                    with(cloudPainter) { draw(size = Size(cloudWidth, cloudHeight), alpha = 0.5f) }
                }
            }

            if (viewModel.gameStarted || viewModel.isGameOver) {
                // Dibujar Tubos
                viewModel.pipes.forEach { pipe ->
                    val capHeight = GameConstants.PIPE_CAP_HEIGHT
                    val capWidth = GameConstants.PIPE_CAP_WIDTH
                    val pipeWidth = GameConstants.PIPE_WIDTH
                    val capOffset = (capWidth - pipeWidth) / 2

                    // --- TUBO SUPERIOR ---
                    // Cuerpo (estirado)
                    withTransform({
                        translate(pipe.x, 0f)
                        rotate(degrees = 180f, pivot = Offset(pipeWidth / 2, (pipe.topHeight - capHeight) / 2))
                    }) {
                        with(pipeBodyPainter) {
                            draw(size = Size(pipeWidth, (pipe.topHeight - capHeight).coerceAtLeast(0f)))
                        }
                    }
                    // Tapa (fija, al final del cuerpo superior)
                    withTransform({
                        translate(pipe.x - capOffset, pipe.topHeight - capHeight)
                        rotate(degrees = 180f, pivot = Offset(capWidth / 2, capHeight / 2))
                    }) {
                        with(pipeCapPainter) {
                            draw(size = Size(capWidth, capHeight))
                        }
                    }

                    // --- TUBO INFERIOR ---
                    // Tapa (fija, arriba)
                    translate(pipe.x - capOffset, pipe.topHeight + GameConstants.PIPE_GAP) {
                        with(pipeCapPainter) {
                            draw(size = Size(capWidth, capHeight))
                        }
                    }
                    // Cuerpo (estirado, debajo de la tapa)
                    translate(pipe.x, pipe.topHeight + GameConstants.PIPE_GAP + capHeight) {
                        with(pipeBodyPainter) {
                            draw(size = Size(pipeWidth, size.height - (pipe.topHeight + GameConstants.PIPE_GAP + capHeight)))
                        }
                    }
                }

                // Mapache (Más grande y estirado)
                val intrinsicSize = raccoonPainter.intrinsicSize
                val aspectRatio = intrinsicSize.width / intrinsicSize.height
                val visualWidth = GameConstants.BIRD_SIZE
                val visualHeight = (visualWidth / aspectRatio) * 1.2f // Estirado verticalmente

                translate(left = (size.width / 4 - visualWidth / 2), top = (viewModel.birdY - visualHeight / 2)) {
                    with(raccoonPainter) { draw(size = Size(visualWidth, visualHeight)) }
                }
            }
        }

        // --- INTERFAZ ---
        
        // Puntuación y Botón de Pausa durante el juego
        if (viewModel.gameStarted && !viewModel.isGameOver) {
            Box(Modifier.fillMaxSize()) {
                // Puntuación (Movida al lado contrario del botón de pausa)
                Text(
                    "${viewModel.score}", 
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 60.dp, start = 20.dp), 
                    style = scoreTextStyle
                )
                
                // Botón de Pausa
                Image(
                    painter = pauseButtonPainter,
                    contentDescription = "Pausar",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 60.dp, end = 20.dp)
                        .width(160.dp)
                        .height(60.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { viewModel.pauseGame() },
                    contentScale = ContentScale.Fit
                )
            }
        }

        // MENÚ PRINCIPAL
        if (!viewModel.gameStarted && !viewModel.isGameOver) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Image(
                    painter = menuLogoPainter,
                    contentDescription = "Flappy Raccoon Logo",
                    modifier = Modifier.fillMaxWidth(1f).height(300.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.height(40.dp))
                
                // Botón PLAY
                Image(
                    painter = playButtonPainter,
                    contentDescription = "PLAY",
                    modifier = Modifier
                        .width(mainButtonWidth)
                        .height(mainButtonHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { viewModel.startGame() },
                    contentScale = ContentScale.Fit
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Botón OPTIONS
                Image(
                    painter = optionsButtonPainter,
                    contentDescription = "OPTIONS",
                    modifier = Modifier
                        .width(mainButtonWidth)
                        .height(mainButtonHeight)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showOptions = true },
                    contentScale = ContentScale.Fit
                )
            }
            
            // Crédito sutil en el menú principal
            Text(
                "Desarrollado por: Alegappy",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // MENÚ DE PAUSA
        if (viewModel.isPaused) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(300.dp), contentAlignment = Alignment.Center) {
                    // Frame de la tabla (Fondo)
                    Image(
                        painter = pauseTablePainter,
                        contentDescription = "Pause Frame",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Home
                        Image(
                            painter = homeButtonPainter,
                            contentDescription = "Home",
                            modifier = Modifier
                                .width(0.dp)
                                .weight(1f)
                                .height(115.dp)
                                .offset(y = (-8).dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.goToMainMenu() },
                            contentScale = ContentScale.Fit
                        )
                        
                        Spacer(Modifier.width(10.dp))
                        
                        // Botón Resume (Movido un poco a la izquierda con offset)
                        Image(
                            painter = resumeButtonPainter,
                            contentDescription = "Resume",
                            modifier = Modifier
                                .width(0.dp)
                                .weight(1f)
                                .height(110.dp)
                                .offset(x = (-10).dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.resumeGame() },
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }

        // COUNTDOWN CON PNGs
        if (viewModel.countdown > 0) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                val currentNumPainter = when (viewModel.countdown) {
                    3 -> num3Painter
                    2 -> num2Painter
                    1 -> num1Painter
                    else -> null
                }
                
                if (currentNumPainter != null) {
                    Image(
                        painter = currentNumPainter,
                        contentDescription = "Countdown ${viewModel.countdown}",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // GAME OVER
        if (viewModel.isGameOver) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Imagen GAME OVER (MÁS GRANDE)
                    Image(
                        painter = gameOverPainter,
                        contentDescription = "GAME OVER",
                        modifier = Modifier.fillMaxWidth(0.95f).height(250.dp),
                        contentScale = ContentScale.Fit
                    )
                    
                    Text(
                        text = "Score: ${viewModel.score}",
                        modifier = Modifier.padding(bottom = 32.dp),
                        style = scoreTextStyle.copy(fontSize = 32.sp)
                    )

                    // Botón Reintentar (MISMO TAMAÑO QUE LOS OTROS)
                    Image(
                        painter = retryButtonPainter,
                        contentDescription = "Reintentar",
                        modifier = Modifier
                            .width(mainButtonWidth)
                            .height(mainButtonHeight)
                            .offset(x = (-10).dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.startGame() },
                        contentScale = ContentScale.Fit
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Botón HOME en Game Over
                    Image(
                        painter = homeButtonPainter,
                        contentDescription = "Menu Principal",
                        modifier = Modifier
                            .width(mainButtonWidth)
                            .height(mainButtonHeight)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { viewModel.goToMainMenu() },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    if (showOptions) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Opciones", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text("Pronto actualizaremos esta pagina", color = Color.White.copy(alpha = 0.7f), fontSize = 18.sp)
                Spacer(Modifier.height(40.dp))

                Image(
                    painter = playButtonPainter,
                    contentDescription = "VOLVER",
                    modifier = Modifier
                        .width(200.dp)
                        .height(80.dp)
                        .clickable { showOptions = false },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
