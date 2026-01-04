package com.example.skinovate.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background

/**
 * Animated button dengan scale animation saat diklik
 */
@Composable
fun AnimatedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit
) {
    var buttonPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_scale"
    )

    Button(
        onClick = {
            buttonPressed = true
            onClick()
        },
        modifier = modifier.scale(scale),
        enabled = enabled,
        colors = colors,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        content()
    }
    
    LaunchedEffect(buttonPressed) {
        if (buttonPressed) {
            kotlinx.coroutines.delay(150)
            buttonPressed = false
        }
    }
}

/**
 * Animated card dengan elevation animation saat hover/click
 */
@Composable
fun AnimatedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 2.dp,
        animationSpec = tween(durationMillis = 200),
        label = "card_elevation"
    )

    Card(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        interactionSource = remember { MutableInteractionSource() }
    ) {
        content()
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(200)
            isPressed = false
        }
    }
}

/**
 * Fade in animation untuk content yang muncul
 */
@Composable
fun FadeInContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)) + 
                slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(durationMillis = 300)
                ),
        exit = fadeOut(animationSpec = tween(durationMillis = 200)),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Scale in animation untuk items dalam list
 */
@Composable
fun ScaleInContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        content()
    }
}

/**
 * Animated icon button dengan scale effect
 */
@Composable
fun AnimatedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    var buttonPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_button_scale"
    )

    IconButton(
        onClick = {
            buttonPressed = true
            onClick()
        },
        modifier = modifier.scale(scale),
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        content()
    }
    
    LaunchedEffect(buttonPressed) {
        if (buttonPressed) {
            kotlinx.coroutines.delay(100)
            buttonPressed = false
        }
    }
}

/**
 * Pulse animation untuk loading atau attention-grabbing elements
 */
@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(modifier = modifier.scale(scale)) {
        content()
    }
}

