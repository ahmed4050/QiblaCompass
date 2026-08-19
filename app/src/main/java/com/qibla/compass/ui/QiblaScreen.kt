package com.qibla.compass.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.qibla.compass.R
import com.qibla.compass.data.AccuracyLevel
import com.qibla.compass.ui.theme.QiblaCompassTheme
import com.qibla.compass.ui.theme.QiblaShapes
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaScreen(
    viewModel: QiblaViewModel = hiltViewModel()
) {
    QiblaCompassTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LocationInfoCard(viewModel)

                Spacer(Modifier.padding(24.dp))

                CompassView(viewModel)

                Spacer(Modifier.padding(24.dp))

                ControlButtons(viewModel)
            }

            if (viewModel.isLoading) {
                LoadingOverlay()
            }

            if (viewModel.hasError) {
                ErrorSnackbar(viewModel)
            }
        }
    }
}

@Composable
private fun LocationInfoCard(viewModel: QiblaViewModel) {
    val location = viewModel.userLocation
    val accuracy = viewModel.accuracyLevel

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = QiblaShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_qibla_direction),
                    contentDescription = stringResource(R.string.qibla_direction),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.qibla_direction),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = viewModel.directionText,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${viewModel.qiblaDirection}°",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(
                    icon = R.drawable.ic_location,
                    label = stringResource(R.string.distance_to_kaaba),
                    value = "${viewModel.distanceToKaaba} كم",
                    color = MaterialTheme.colorScheme.tertiary
                )

                InfoItem(
                    icon = R.drawable.ic_accuracy,
                    label = stringResource(R.string.accuracy),
                    value = accuracy?.name ?: "---",
                    color = when (accuracy) {
                        AccuracyLevel.HIGH -> MaterialTheme.colorScheme.primary
                        AccuracyLevel.MEDIUM -> MaterialTheme.colorScheme.tertiary
                        AccuracyLevel.LOW -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            location?.let { loc ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.coordinates_format, loc.latitude, loc.longitude),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    icon: Int,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
    }
}

@Composable
fun CompassView(viewModel: QiblaViewModel) {
    val size = 280.dp
    val relativeAngle = viewModel.relativeQiblaAngle
    val deviceAzimuth = viewModel.deviceAzimuth

    val animatedAngle by animateFloatAsState(
        targetValue = relativeAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "qiblaAngle"
    )

    val textMeasurer = rememberTextMeasurer()

    val circleColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val tickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    val cardinalColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size
            drawCompassCircle(
                size = canvasSize,
                deviceAzimuth = deviceAzimuth,
                circleColor = circleColor,
                tickColor = tickColor,
                textColor = textColor,
                cardinalColor = cardinalColor,
                textMeasurer = textMeasurer
            )
        }

        QiblaArrow(
            angle = animatedAngle,
            size = size,
            arrowColor = MaterialTheme.colorScheme.primary,
            tipColor = MaterialTheme.colorScheme.primaryContainer
        )

        CircleIndicator()
    }
}

@Composable
fun QiblaArrow(
    angle: Float,
    size: Dp,
    arrowColor: Color,
    tipColor: Color
) {
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                rotationZ = angle
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size
            val arrowSize = canvasSize.width * 0.85f
            val arrowHeight = arrowSize * 0.9f
            val centerX = canvasSize.width / 2f
            val centerY = canvasSize.height / 2f
            val halfWidth = arrowSize / 6f

            val path = Path().apply {
                moveTo(centerX, centerY - arrowHeight / 2f)
                lineTo(centerX - halfWidth, centerY + arrowHeight / 4f)
                lineTo(centerX + halfWidth, centerY + arrowHeight / 4f)
                close()
            }

            drawPath(
                path = path,
                color = arrowColor
            )

            val tipPath = Path().apply {
                moveTo(centerX, centerY - arrowHeight / 2f)
                lineTo(centerX - halfWidth * 1.5f, centerY - arrowHeight / 2f + halfWidth * 2f)
                lineTo(centerX + halfWidth * 1.5f, centerY - arrowHeight / 2f + halfWidth * 2f)
                close()
            }

            drawPath(
                path = tipPath,
                color = tipColor
            )
        }
    }
}

@Composable
fun CircleIndicator() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
                .align(Alignment.Center)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCompassCircle(
    size: Size,
    deviceAzimuth: Float,
    circleColor: Color,
    tickColor: Color,
    textColor: Color,
    cardinalColor: Color,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = size.width / 2f - 20f

    drawCircle(
        color = circleColor,
        center = center,
        radius = radius + 20f,
        style = Stroke(width = 40.dp.toPx())
    )

    // علامات الدرجات (كل 15 درجة)
    for (i in 0..35) {
        val angle = Math.toRadians((i * 15 - deviceAzimuth + 360) % 360 - 90.0)
        val innerRadius = radius - 15f
        val outerRadius = radius

        val start = Offset(
            center.x + innerRadius * cos(angle).toFloat(),
            center.y + innerRadius * sin(angle).toFloat()
        )
        val end = Offset(
            center.x + outerRadius * cos(angle).toFloat(),
            center.y + outerRadius * sin(angle).toFloat()
        )

        drawLine(
            color = tickColor,
            start = start,
            end = end,
            strokeWidth = if (i % 3 == 0) 1.5.dp.toPx() else 1.dp.toPx()
        )
    }

    // الاتجاهات الرئيسية مع النصوص
    val directions = listOf(
        0 to "شمال",
        90 to "شرق",
        180 to "جنوب",
        270 to "غرب"
    )

    directions.forEach { (deg, label) ->
        val angle = Math.toRadians((deg - deviceAzimuth + 360) % 360 - 90.0)
        val textRadius = radius - 40f
        val textX = center.x + textRadius * cos(angle).toFloat()
        val textY = center.y + textRadius * sin(angle).toFloat()

        val style = TextStyle(
            color = cardinalColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        val layout = textMeasurer.measure(label, style)

        drawText(
            textLayoutResult = layout,
            topLeft = Offset(
                textX - layout.size.width / 2f,
                textY - layout.size.height / 2f
            )
        )
    }

    // إحداثيات الكعبة في المركز
    val kaabaText = "الكعبة"
    val kaabaStyle = TextStyle(
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
    val kaabaLayout = textMeasurer.measure(kaabaText, kaabaStyle)
    drawText(
        textLayoutResult = kaabaLayout,
        topLeft = Offset(
            center.x - kaabaLayout.size.width / 2f,
            center.y - kaabaLayout.size.height / 2f - radius / 2f
        )
    )
}

@Composable
fun ControlButtons(viewModel: QiblaViewModel) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = { viewModel.refreshLocation() },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp),
            shape = QiblaShapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.refresh_location),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        OutlinedButton(
            onClick = { viewModel.onCalibrationComplete() },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp),
            shape = QiblaShapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_calibration),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.calibrate_compass),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.determining_location),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ErrorSnackbar(viewModel: QiblaViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }
    val retryText = stringResource(R.string.retry)

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = retryText,
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.refreshLocation()
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 90.dp)
    )
}

@Composable
fun CompassScreen() {
    QiblaScreen()
}