/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.woof

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.woof.data.Dog
import com.example.woof.data.dogs
import com.example.woof.ui.theme.WoofTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WoofTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF1A1A2E) // Dark magical background
                ) {
                    WoofApp()
                }
            }
        }
    }
}

/**
 * Composable that displays an app bar and a list of dogs.
 */
@Composable
fun WoofApp() {
    // Animated background colors
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val backgroundAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backgroundAnimation"
    )

    val dynamicBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E).copy(alpha = 0.9f + backgroundAnimation * 0.1f),
            Color(0xFF16213E).copy(alpha = 0.8f + backgroundAnimation * 0.2f),
            Color(0xFF0F3460).copy(alpha = 0.7f + backgroundAnimation * 0.3f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dynamicBackground)
    ) {
        // Floating particles effect
        FloatingParticles()

        Scaffold(
            topBar = { WoofTopAppBar() },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(dogs) { index, dog ->
                    val delay = index * 100
                    DogItem(
                        dog = dog,
                        index = index,
                        animationDelay = delay,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingParticles() {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    repeat(12) { index ->
        val offsetX by infiniteTransition.animateFloat(
            initialValue = (index * 30).toFloat(),
            targetValue = (index * 30 + 100).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + index * 200,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particleX$index"
        )

        val offsetY by infiniteTransition.animateFloat(
            initialValue = (index * 60).toFloat(),
            targetValue = (index * 60 + 150).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4000 + index * 300,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "particleY$index"
        )

        val particleEmojis = listOf("🐾", "💖", "⭐", "🌟", "💫", "✨")

        Box(
            modifier = Modifier
                .offset(x = offsetX.dp, y = offsetY.dp)
                .size(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = particleEmojis[index % particleEmojis.size],
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

/**
 * Enhanced dog item with spectacular animations and effects
 */
@Composable
fun DogItem(
    dog: Dog,
    index: Int,
    animationDelay: Int,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }

    // Multiple animation states
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            isHovered -> 1.05f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isFavorite) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation"
    )

    val cardColor by animateColorAsState(
        targetValue = if (isFavorite) {
            Color(0xFFFF6B9D).copy(alpha = 0.95f)
        } else {
            Color.White.copy(alpha = 0.95f)
        },
        animationSpec = tween(600),
        label = "cardColor"
    )

    // Gradient colors for variety
    val gradientColors = listOf(
        listOf(Color(0xFFFF6B9D), Color(0xFFFFB3E6), Color(0xFFE8F5E8)),
        listOf(Color(0xFF4ECDC4), Color(0xFF44A08D), Color(0xFF093637)),
        listOf(Color(0xFFFFD93D), Color(0xFF6BCF7F), Color(0xFF4D9DE0)),
        listOf(Color(0xFFE15FED), Color(0xFF6F42C1), Color(0xFF495057)),
        listOf(Color(0xFFFF8A80), Color(0xFFFF5722), Color(0xFFBF360C))
    )

    val cardGradient = gradientColors[index % gradientColors.size]

    Card(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (isHovered) 16.dp else 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = cardGradient[0].copy(alpha = 0.3f),
                spotColor = cardGradient[1].copy(alpha = 0.3f)
            )
            .clickable {
                isPressed = !isPressed
                isHovered = !isHovered
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box {
            // Animated gradient border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(cardGradient)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Enhanced dog icon with magical effects
                EnhancedDogIcon(
                    dogIcon = dog.imageResourceId,
                    isFavorite = isFavorite,
                    gradientColors = cardGradient
                )

                Spacer(modifier = Modifier.width(20.dp))

                // Enhanced dog information
                EnhancedDogInformation(
                    dogName = dog.name,
                    dogAge = dog.age,
                    isFavorite = isFavorite,
                    modifier = Modifier.weight(1f)
                )

                // Multiple action buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Love button with pulse effect
                    FloatingActionButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.size(56.dp),
                        containerColor = if (isFavorite) Color(0xFFFF6B9D) else Color(0xFFF8F9FA),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.White else Color(0xFF6C757D),
                            modifier = Modifier
                                .size(28.dp)
                                .rotate(rotation)
                        )
                    }

                    // Action menu button
                    IconButton(
                        onClick = { /* TODO: Show dog details */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0xFF6C5CE7).copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            tint = Color(0xFF6C5CE7),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Magical sparkles decoration
            MagicalSparkles(
                isActive = isFavorite,
                colors = cardGradient
            )
        }
    }
}

@Composable
fun EnhancedDogIcon(
    @DrawableRes dogIcon: Int,
    isFavorite: Boolean,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    val iconScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconScale"
    )

    Box(
        modifier = modifier.size(90.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow effect
        if (isFavorite) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                gradientColors[0].copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Gradient border
        Box(
            modifier = Modifier
                .size(82.dp)
                .background(
                    brush = Brush.sweepGradient(gradientColors),
                    shape = CircleShape
                )
                .scale(iconScale)
        )

        // White inner border
        Box(
            modifier = Modifier
                .size(78.dp)
                .background(Color.White, CircleShape)
        )

        // Dog image
        Image(
            modifier = Modifier
                .size(74.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            painter = painterResource(dogIcon),
            contentDescription = null
        )

        // Status indicator
        Box(
            modifier = Modifier
                .size(20.dp)
                .offset(x = 28.dp, y = (-28).dp)
                .background(
                    color = if (isFavorite) Color(0xFF00D2FF) else Color(0xFF32D74B),
                    shape = CircleShape
                )
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isFavorite) "💖" else "🐕",
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun EnhancedDogInformation(
    @StringRes dogName: Int,
    dogAge: Int,
    isFavorite: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor by animateColorAsState(
        targetValue = if (isFavorite) Color.White else Color(0xFF2E2E2E),
        label = "textColor"
    )

    val subtitleColor by animateColorAsState(
        targetValue = if (isFavorite) Color.White.copy(alpha = 0.9f) else Color(0xFF666666),
        label = "subtitleColor"
    )

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(dogName),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = 22.sp
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            if (isFavorite) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "👑",
                    fontSize = 18.sp
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Cake,
                contentDescription = null,
                tint = subtitleColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.years_old, dogAge),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = subtitleColor,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // Enhanced personality with icons
        val personalityData = listOf(
            Triple("Playful", "🎾", Color(0xFF4ECDC4)),
            Triple("Sleepy", "😴", Color(0xFF6C5CE7)),
            Triple("Energetic", "⚡", Color(0xFFFFD93D)),
            Triple("Cuddly", "🤗", Color(0xFFFF6B9D)),
            Triple("Brave", "🦸", Color(0xFF00D2FF)),
            Triple("Sweet", "🍯", Color(0xFFFF8A80))
        )

        val personality = personalityData.random()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = personality.third.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = personality.second,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = personality.first,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = subtitleColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MagicalSparkles(
    isActive: Boolean,
    colors: List<Color>
) {
    if (isActive) {
        val infiniteTransition = rememberInfiniteTransition(label = "sparkles")

        repeat(6) { index ->
            val sparkleScale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 800 + index * 100,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sparkleScale$index"
            )

            val sparkleRotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000 + index * 200,
                        easing = LinearEasing
                    )
                ),
                label = "sparkleRotation$index"
            )

            val positions = listOf(
                Pair(20.dp, 20.dp), Pair(60.dp, 30.dp), Pair(100.dp, 25.dp),
                Pair(30.dp, 80.dp), Pair(80.dp, 90.dp), Pair(120.dp, 70.dp)
            )

            Box(
                modifier = Modifier
                    .offset(x = positions[index].first, y = positions[index].second)
                    .scale(sparkleScale)
                    .rotate(sparkleRotation)
            ) {
                Text(
                    text = "✨",
                    fontSize = 16.sp,
                    color = colors[index % colors.size]
                )
            }
        }
    }
}

/**
 * Spectacular top app bar with animated elements
 */
@Composable
fun WoofTopAppBar(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "topbar")

    val logoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        ),
        label = "logoRotation"
    )

    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF6B9D),
                                    Color(0xFF4ECDC4),
                                    Color(0xFFFFD93D)
                                )
                            ),
                            shape = CircleShape
                        )
                        .rotate(logoRotation),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(R.drawable.ic_woof_logo),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "🐾 WOOF MAGIC 🌟",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 24.sp
                    )
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        modifier = modifier
    )
}

/**
 * Preview functions
 */
@Preview(showBackground = true)
@Composable
fun WoofPreview() {
    WoofTheme(darkTheme = false) {
        WoofApp()
    }
}

@Preview(showBackground = true)
@Composable
fun WoofDarkThemePreview() {
    WoofTheme(darkTheme = true) {
        WoofApp()
    }
}