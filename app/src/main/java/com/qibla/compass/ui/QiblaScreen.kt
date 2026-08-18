package com.qibla.compass.ui

import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.animateFloat
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.qibla.compass.R
import com.qibla.compass.ui.theme.QiblaCompassTheme
import com.qibla.compass.ui.theme.QiblaShapes
import com.qibla.compass.ui.theme.QiblaTypography
import kotlinx.coroutines.launch

@Composable
fun QiblaScreen(
    viewModel: QiblaViewModel = hiltViewModel()
) {
    QiblaCompassTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // الخلفية
            BackgroundLayer()

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // معلومات الموقع والقبلة
                LocationInfoCard(viewModel)

                Spacer(Modifier.padding(32.dp))

                // البوصلة الرئيسية
                CompassView(viewModel)

                Spacer(Modifier.padding(32.dp))

                // أزرار التحكم
                ControlButtons(viewModel)
            }

            // مؤشر التحميل
            if (viewModel.isLoading) {
                LoadingOverlay()
            }

            // رسائل الخطأ
            if (viewModel.hasError) {
                ErrorSnackbar(viewModel)
            }
        }
    }
}

@Composable
private fun BackgroundLayer() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    )
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
            // اتجاه القبلة
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource("ic_qibla_direction.xml"),
                    contentDescription = "اتجاه القبلة",
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
                text = "${viewModel.qiblaDirection:.1f}°",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // المسافة والموقع
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(
                    icon = "ic_location.xml",
                    label = stringResource(R.string.distance_to_kaaba),
                    value = "${viewModel.distanceToKaaba:.0f} كم",
                    color = MaterialTheme.colorScheme.tertiary
                )

                InfoItem(
                    icon = "ic_accuracy.xml",
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
    icon: String,
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

    // أنيميشن سلسة للسهم
    val animatedAngle by animateFloatAsState(
        targetValue = relativeAngle,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // دائرة البوصلة الخارجية
        Canvas(modifier = Modifier.size(size)) {
            drawCompassCircle(
                size = Size(size.toPx(), size.toPx()),
                deviceAzimuth = deviceAzimuth
            )
        }

        // سهم القبلة الدوار
        QiblaArrow(
            angle = animatedAngle,
            size = size
        )

        // نقطة المركز
        CircleIndicator()
    }
}

@Composable
fun QiblaArrow(
    angle: Float,
    size: Dp
) {
    val arrowSize = (size.toPx() * 0.85)
    val arrowHeight = arrowSize * 0.9

    Box(
        modifier = Modifier
            .size(size)
            .rotate(angle)
            .graphicsLayer {
                transformOrigin = androidx.compose.ui.graphicsLayer.TransformOrigin(
                    androidx.compose.ui.geometry.Offset(size.toPx() / 2, size.toPx() / 2)
                )
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val centerX = size.toPx() / 2
            val centerY = size.toPx() / 2
            val halfWidth = arrowSize / 6

            val path = Path().apply {
                moveTo(centerX, centerY - arrowHeight / 2)
                lineTo(centerX - halfWidth, centerY + arrowHeight / 4)
                lineTo(centerX + halfWidth, centerY + arrowHeight / 4)
                close()
            }

            drawPath(
                path = path,
                color = MaterialTheme.colorScheme.primary,
                style = Fill
            )

            // رأس السهم (مثلث)
            val tipPath = Path().apply {
                moveTo(centerX, centerY - arrowHeight / 2)
                lineTo(centerX - halfWidth * 1.5f, centerY - arrowHeight / 2 + halfWidth * 2)
                lineTo(centerX + halfWidth * 1.5f, centerY - arrowHeight / 2 + halfWidth * 2)
                close()
            }

            drawPath(
                path = tipPath,
                color = MaterialTheme.colorScheme.primaryContainer,
                style = Fill
            )
        }

        // نص "قبلة" على السهم
        Text(
            text = "الكعبة",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .padding(top = (size.toPx() * 0.15).dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun CircleIndicator() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .align(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
                .align(Alignment.Center)
        )
    }
}

private fun Canvas.drawCompassCircle(size: Size, deviceAzimuth: Float) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.width / 2 - 20

    // دائرة خلفية
    drawCircle(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        center = center,
        radius = radius + 20,
        style = Stroke(width = 40.dp.toPx())
    )

    // علامات الدرجات
    val degreePaint = Paint().apply {
        color = MaterialTheme.colorScheme.onSurface.toArgb()
        strokeWidth = 1.dp.toPx()
        isAntiAlias = true
        textSize = 12.dp.toPx()
        typeface = androidx.compose.ui.text.font.Typeface.DEFAULT_BOLD
    }

    for (i in 0..35 step 15) {
        val angle = Math.toRadians((i - deviceAzimuth + 360) % 360 - 90)
        val innerRadius = radius - 15
        val outerRadius = radius

        val startX = center.x + innerRadius * cos(angle)
        val startY = center.y + innerRadius * sin(angle)
        val endX = center.x + outerRadius * cos(angle)
        val endY = center.y + outerRadius * sin(angle)

        drawLine(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 1.5.dp.toPx()
        )

        // نص الدرجة
        if (i % 45 == 0) {
            val textRadius = radius - 30
            val textX = center.x + textRadius * cos(angle)
            val textY = center.y + textRadius * sin(angle)

            val label = when (i) {
                0 -> "ش"
                90 -> "ش"
                180 -> "ج"
                270 -> "غ"
                else -> i.toString()
            }

            drawText(
                text = label,
                x = textX - degreePaint.measureText(label) / 2,
                y = textY + degreePaint.textSize / 3,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                style = degreePaint
            )
        }
    }

    // علامات الاتجاهات الرئيسية
    val cardinalPaint = Paint().apply {
        color = MaterialTheme.colorScheme.primary.toArgb()
        strokeWidth = 2.dp.toPx()
        isAntiAlias = true
        textSize = 16.dp.toPx()
        typeface = androidx.compose.ui.text.font.Typeface.DEFAULT_BOLD
    }

    val directions = listOf(
        0f to "شمال",
        90f to "شرق",
        180f to "جنوب",
        270f to "غرب"
    )

    directions.forEach { (deg, label) ->
        val angle = Math.toRadians((deg - deviceAzimuth + 360) % 360 - 90)
        val textRadius = radius - 50
        val textX = center.x + textRadius * cos(angle)
        val textY = center.y + textRadius * sin(angle)

        drawText(
            text = label,
            x = textX - cardinalPaint.measureText(label) / 2,
            y = textY + cardinalPaint.textSize / 3,
            color = MaterialTheme.colorScheme.primary,
            style = cardinalPaint
        )
    }
}

@Composable
fun ControlButtons(viewModel: QiblaViewModel) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // زر إعادة التحديد
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
                    painter = painterResource("ic_refresh.xml"),
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

        // زر المعايرة
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
                    painter = painterResource("ic_calibration.xml"),
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    SnackbarHost(hostState = snackbarHostState)

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = stringResource(R.string.retry),
                duration = SnackbarDuration.Indefinite
            ) {
                viewModel.refreshLocation()
            }
        }
    }
}

@Composable
fun CompassScreen() {
    QiblaScreen()
}