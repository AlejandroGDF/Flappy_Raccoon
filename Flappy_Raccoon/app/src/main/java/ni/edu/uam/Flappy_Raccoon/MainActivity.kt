package ni.edu.uam.flappy_raccoon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import ni.edu.uam.flappy_raccoon.ui.theme.Juego_FlappyBirdTheme
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

@Composable
fun GameScreen(viewModel: GameViewModel) {
    // Cargamos todos los recursos visuales aquí para tenerlos a mano
    val pipeBodyPainter = painterResource(id = R.drawable.pipe_low_texture)
    val pipeCapPainter = painterResource(id = R.drawable.pipe_top_texture)
    val cloudPainter = painterResource(id = R.drawable.cloud)
    val menuLogoPainter = painterResource(id = R.drawable.menutexto)
    val playButtonPainter = painterResource(id = R.drawable.boton_play)
    val optionsButtonPainter = painterResource(id = R.drawable.boton_options)
    val skinButtonPainter = painterResource(id = R.drawable.boton_skin)
    val acceptButtonPainter = painterResource(id = R.drawable.boton_aceptar)
    val leftArrowPainter = painterResource(id = R.drawable.left_select)
    val rightArrowPainter = painterResource(id = R.drawable.right_select)
    val retryButtonPainter = painterResource(id = R.drawable.boton_retry)
    val pauseButtonPainter = painterResource(id = R.drawable.boton_pause)
    val pauseTablePainter = painterResource(id = R.drawable.tabla_frame)
    val homeButtonPainter = painterResource(id = R.drawable.boton_home)
    val resumeButtonPainter = painterResource(id = R.drawable.boton_resume)
    
    val currentSkin = viewModel.skins[viewModel.currentSkinIndex]
    val gameOverPainter = painterResource(id = currentSkin.gameOverRes)
    val selectSkinTitlePainter = painterResource(id = R.drawable.texto_seleccionar)
    
    val num1Painter = painterResource(id = R.drawable.num1)
    val num2Painter = painterResource(id = R.drawable.num2)
    val num3Painter = painterResource(id = R.drawable.num3)

    val currentRaccoonPainter = painterResource(id = currentSkin.imageRes)

    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }
    var showOptions by remember { mutableStateOf(false) }
    val clouds = remember { mutableStateListOf<Cloud>() }

    // --- COLOR DE FONDO DINÁMICO ---
    val skyColor = if (viewModel.isNightMode) Color(0xFF1A1A2E) else Color(0xFF70C5CE)

    val scoreTextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 64.sp,
        color = Color.White,
        shadow = Shadow(color = Color.Black, offset = Offset(4f, 4f), blurRadius = 2f)
    )

    val mainButtonWidth = 320.dp
    val mainButtonHeight = 110.dp

    // Generamos unas nubes aleatorias al inicio para que el fondo no se vea tan vacío
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
            .background(skyColor)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { 
                if (viewModel.gameStarted && !viewModel.isGameOver && !viewModel.isPaused && viewModel.countdown == 0) viewModel.jump() 
            }
            .onGloballyPositioned { coordinates ->
                screenWidth = coordinates.size.width.toFloat()
                screenHeight = coordinates.size.height.toFloat()
                // Le pasamos las dimensiones al ViewModel para que sepa dónde poner los tubos
                viewModel.onSizeChanged(screenWidth, screenHeight)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // MOSTRAR NUBES SOLO SI ESTÁ ACTIVO
            if (viewModel.showClouds) {
                clouds.forEach { cloud ->
                    val cloudWidth = 300f * cloud.scale
                    val cloudHeight = 180f * cloud.scale
                    translate(cloud.x - cloudWidth/2, cloud.y) {
                        with(cloudPainter) { draw(size = Size(cloudWidth, cloudHeight), alpha = 0.5f) }
                    }
                }
            }

            if (viewModel.gameStarted || viewModel.isGameOver) {
                // Lógica para dibujar los tubos de arriba y abajo
                viewModel.pipes.forEach { pipe ->
                    val capHeight = GameConstants.PIPE_CAP_HEIGHT
                    val capWidth = GameConstants.PIPE_CAP_WIDTH
                    val pipeWidth = GameConstants.PIPE_WIDTH
                    val capOffset = (capWidth - pipeWidth) / 2

                    withTransform({
                        translate(pipe.x, 0f)
                        rotate(degrees = 180f, pivot = Offset(pipeWidth / 2, (pipe.topHeight - capHeight) / 2))
                    }) {
                        with(pipeBodyPainter) {
                            draw(size = Size(pipeWidth, (pipe.topHeight - capHeight).coerceAtLeast(0f)))
                        }
                    }
                    withTransform({
                        translate(pipe.x - capOffset, pipe.topHeight - capHeight)
                        rotate(degrees = 180f, pivot = Offset(capWidth / 2, capHeight / 2))
                    }) {
                        with(pipeCapPainter) {
                            draw(size = Size(capWidth, capHeight))
                        }
                    }

                    translate(pipe.x - capOffset, pipe.topHeight + GameConstants.PIPE_GAP) {
                        with(pipeCapPainter) {
                            draw(size = Size(capWidth, capHeight))
                        }
                    }
                    translate(pipe.x, pipe.topHeight + GameConstants.PIPE_GAP + capHeight) {
                        with(pipeBodyPainter) {
                            draw(size = Size(pipeWidth, size.height - (pipe.topHeight + GameConstants.PIPE_GAP + capHeight)))
                        }
                    }
                }

                val intrinsicSize = currentRaccoonPainter.intrinsicSize
                val aspectRatio = intrinsicSize.width / intrinsicSize.height
                val visualWidth = GameConstants.RACCOON_SIZE
                val visualHeight = (visualWidth / aspectRatio) * 1.2f 

                // El mapache se queda fijo en X (un cuarto de la pantalla) y solo se mueve en Y
                translate(left = (size.width / 4 - visualWidth / 2), top = (viewModel.raccoonY - visualHeight / 2)) {
                    with(currentRaccoonPainter) { draw(size = Size(visualWidth, visualHeight)) }
                }
            }
        }

        // Interfaz activa durante la partida (puntos y pausa)
        if (viewModel.gameStarted && !viewModel.isGameOver) {
            Box(Modifier.fillMaxSize()) {
                Text(
                    "${viewModel.score}", 
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 60.dp, start = 20.dp), 
                    style = scoreTextStyle
                )
                
                Image(
                    painter = pauseButtonPainter,
                    contentDescription = null,
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

        // Menú principal - Solo se muestra si no hemos empezado
        if (!viewModel.gameStarted && !viewModel.isGameOver && !viewModel.showSkinSelection) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Image(painter = menuLogoPainter, contentDescription = null, modifier = Modifier.fillMaxWidth(1f).height(300.dp), contentScale = ContentScale.Fit)
                Spacer(Modifier.height(30.dp))
                
                Image(
                    painter = playButtonPainter,
                    contentDescription = null,
                    modifier = Modifier.width(mainButtonWidth).height(mainButtonHeight).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.startGame() },
                    contentScale = ContentScale.Fit
                )
                
                Spacer(Modifier.height(16.dp))

                Image(
                    painter = skinButtonPainter,
                    contentDescription = null,
                    modifier = Modifier.width(mainButtonWidth).height(mainButtonHeight).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.showSkinSelection = true },
                    contentScale = ContentScale.Fit
                )
                
                Spacer(Modifier.height(16.dp))
                
                Image(
                    painter = optionsButtonPainter,
                    contentDescription = null,
                    modifier = Modifier.width(mainButtonWidth).height(mainButtonHeight).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showOptions = true },
                    contentScale = ContentScale.Fit
                )
            }
            
            Text(
                "Desarrollado por: Alegappy",
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
                style = TextStyle(color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            )
        }

        if (viewModel.isPaused) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(300.dp), contentAlignment = Alignment.Center) {
                    Image(painter = pauseTablePainter, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = homeButtonPainter, contentDescription = null, modifier = Modifier.width(0.dp).weight(1f).height(115.dp).offset(y = (-8).dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.goToMainMenu() }, contentScale = ContentScale.Fit)
                        Spacer(Modifier.width(10.dp))
                        Image(painter = resumeButtonPainter, contentDescription = null, modifier = Modifier.width(0.dp).weight(1f).height(110.dp).offset(x = (-10).dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.resumeGame() }, contentScale = ContentScale.Fit)
                    }
                }
            }
        }

        if (viewModel.countdown > 0) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                val p = when (viewModel.countdown) { 3 -> num3Painter; 2 -> num2Painter; 1 -> num1Painter; else -> null }
                if (p != null) Image(painter = p, contentDescription = null, modifier = Modifier.size(200.dp), contentScale = ContentScale.Fit)
            }
        }

        // Pantalla de Game Over
        if (viewModel.isGameOver) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painter = gameOverPainter, contentDescription = null, modifier = Modifier.fillMaxWidth(0.95f).height(250.dp), contentScale = ContentScale.Fit)
                    Text(text = "Score: ${viewModel.score}", modifier = Modifier.padding(bottom = 32.dp), style = scoreTextStyle.copy(fontSize = 32.sp))
                    Image(painter = retryButtonPainter, contentDescription = null, modifier = Modifier.width(mainButtonWidth).height(mainButtonHeight).offset(x = (-10).dp).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.startGame() }, contentScale = ContentScale.Fit)
                    Spacer(Modifier.height(16.dp))
                    Image(painter = homeButtonPainter, contentDescription = null, modifier = Modifier.width(mainButtonWidth).height(mainButtonHeight).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.goToMainMenu() }, contentScale = ContentScale.Fit)
                }
            }
        }

        // Selector de personajes/skins
        if (viewModel.showSkinSelection) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Image(
                        painter = selectSkinTitlePainter,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(1f).height(250.dp),
                        contentScale = ContentScale.Fit
                    )
                    
                    Spacer(Modifier.height(10.dp))
                    
                    Text(
                        viewModel.skins[viewModel.currentSkinIndex].name,
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = TextStyle(shadow = Shadow(Color.Black, offset = Offset(2f, 2f), blurRadius = 2f))
                    )

                    Spacer(Modifier.height(40.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = leftArrowPainter, 
                            contentDescription = "Anterior", 
                            modifier = Modifier
                                .size(100.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.prevSkin() }, 
                            contentScale = ContentScale.Fit
                        )
                        
                        Image(
                            painter = painterResource(id = viewModel.skins[viewModel.currentSkinIndex].imageRes),
                            contentDescription = "Skin actual", 
                            modifier = Modifier.size(180.dp)
                        )
                        
                        Image(
                            painter = rightArrowPainter, 
                            contentDescription = "Siguiente", 
                            modifier = Modifier
                                .size(100.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.nextSkin() }, 
                            contentScale = ContentScale.Fit
                        )
                    }
                    
                    Spacer(Modifier.height(60.dp))
                    
                    Image(
                        painter = acceptButtonPainter, 
                        contentDescription = "Aceptar", 
                        modifier = Modifier
                            .width(320.dp)
                            .height(110.dp)
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { viewModel.showSkinSelection = false }, 
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    // --- NUEVA PANTALLA DE OPCIONES ---
    if (showOptions) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Text("AJUSTES", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black, style = TextStyle(shadow = Shadow(Color.DarkGray, offset = Offset(4f, 4f))))
                
                Spacer(Modifier.height(40.dp))

                // SECCIÓN DIFICULTAD
                Text("DIFICULTAD", color = Color.Yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    DifficultyButton("FÁCIL", viewModel.difficulty == Difficulty.PRACTICE) { viewModel.difficulty = Difficulty.PRACTICE }
                    DifficultyButton("NORMAL", viewModel.difficulty == Difficulty.NORMAL) { viewModel.difficulty = Difficulty.NORMAL }
                    DifficultyButton("PRO", viewModel.difficulty == Difficulty.HARDCORE) { viewModel.difficulty = Difficulty.HARDCORE }
                }

                Spacer(Modifier.height(40.dp))

                // SECCIÓN ESCENARIO
                Text("ESCENARIO", color = Color.Yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                
                OptionToggle("MODO NOCHE", viewModel.isNightMode) { viewModel.isNightMode = !viewModel.isNightMode }
                Spacer(Modifier.height(12.dp))
                OptionToggle("MOSTRAR NUBES", viewModel.showClouds) { viewModel.showClouds = !viewModel.showClouds }

                Spacer(Modifier.height(60.dp))

                Image(
                    painter = acceptButtonPainter, 
                    contentDescription = "Cerrar", 
                    modifier = Modifier.width(260.dp).height(90.dp).clickable { showOptions = false }, 
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun DifficultyButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF4CAF50) else Color.DarkGray)
            .border(2.dp, if (selected) Color.White else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OptionToggle(label: String, active: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth(0.8f)
            .height(60.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .clickable { onToggle() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Box(
            Modifier
                .size(45.dp, 25.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (active) Color(0xFF4CAF50) else Color.Gray)
                .padding(4.dp),
            contentAlignment = if (active) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(Modifier.size(17.dp).clip(RoundedCornerShape(50)).background(Color.White))
        }
    }
}

data class Cloud(val x: Float, val y: Float, val scale: Float)
