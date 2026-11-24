package com.PPBM.lentera.core.designsystem.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.PPBM.lentera.core.designsystem.theme.LenteraNavy
import com.PPBM.lentera.core.designsystem.theme.NavyDark
import com.PPBM.lentera.core.designsystem.theme.PlusJakartaSans
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@Composable
fun ProfilePhotoCropDialog(
    sourceUri: Uri,
    onDismiss: () -> Unit,
    onCropSuccess: (croppedUri: Uri, croppedBytes: ByteArray) -> Unit
) {
    val context = LocalContext.current
    var sourceBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(sourceUri) {
        isLoading = true
        sourceBitmap = loadAndSampleBitmap(context, sourceUri)
        isLoading = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading || sourceBitmap == null) {
                CircularProgressIndicator(color = Color.White)
            } else {
                CropContent(
                    bitmap = sourceBitmap!!,
                    onDismiss = onDismiss,
                    onConfirmCrop = { scale, panX, panY, rotation, cropBoxPx ->
                        val cropped = performCrop(
                            bitmap = sourceBitmap!!,
                            scale = scale,
                            panX = panX,
                            panY = panY,
                            rotation = rotation,
                            cropBoxPx = cropBoxPx
                        )
                        if (cropped != null) {
                            val stream = ByteArrayOutputStream()
                            cropped.compress(Bitmap.CompressFormat.JPEG, 92, stream)
                            val bytes = stream.toByteArray()

                            val tempFile = File(context.cacheDir, "crop_avatar_${System.currentTimeMillis()}.jpg")
                            FileOutputStream(tempFile).use { it.write(bytes) }
                            val croppedUri = Uri.fromFile(tempFile)

                            onCropSuccess(croppedUri, bytes)
                        } else {
                            onDismiss()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CropContent(
    bitmap: Bitmap,
    onDismiss: () -> Unit,
    onConfirmCrop: (scale: Float, panX: Float, panY: Float, rotation: Float, cropBoxPx: Float) -> Unit
) {
    val density = LocalDensity.current
    val cropBoxSizeDp = 280.dp
    val cropBoxSizePx = with(density) { cropBoxSizeDp.toPx() }

    var scale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Batal",
                    tint = Color.White
                )
            }
            Text(
                text = "Sesuaikan Foto Profil",
                color = Color.White,
                fontFamily = PlusJakartaSans,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            IconButton(
                onClick = {
                    onConfirmCrop(scale, panOffset.x, panOffset.y, rotationAngle, cropBoxSizePx)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Gunakan Foto",
                    tint = Color(0xFF22C55E)
                )
            }
        }

        // Viewport Preview & Gesture Area
        Box(
            modifier = Modifier
                .size(cropBoxSizeDp)
                .clip(CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 4.0f)
                        panOffset += pan
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                drawContext.canvas.save()
                drawContext.canvas.translate(canvasWidth / 2f + panOffset.x, canvasHeight / 2f + panOffset.y)
                drawContext.canvas.rotate(rotationAngle)
                drawContext.canvas.scale(scale, scale)

                val bmpW = imageBitmap.width.toFloat()
                val bmpH = imageBitmap.height.toFloat()
                val baseScale = maxOf(canvasWidth / bmpW, canvasHeight / bmpH)
                val destW = bmpW * baseScale
                val destH = bmpH * baseScale

                drawImage(
                    image = imageBitmap,
                    dstOffset = IntOffset((-destW / 2f).roundToInt(), (-destH / 2f).roundToInt()),
                    dstSize = IntSize(destW.roundToInt(), destH.roundToInt())
                )
                drawContext.canvas.restore()
            }
        }

        // Petunjuk & Kontrol Zoom / Putar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Geser & cubit untuk menyesuaikan posisi",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontFamily = PlusJakartaSans
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Zoom",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = PlusJakartaSans
                )
                Slider(
                    value = scale,
                    onValueChange = { scale = it },
                    valueRange = 0.8f..3.5f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )
                IconButton(
                    onClick = {
                        rotationAngle = (rotationAngle + 90f) % 360f
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Putar 90 Derajat",
                        tint = Color.White
                    )
                }
            }

            // Tombol Aksi Bawah
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                ) {
                    Text("Batal", fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        onConfirmCrop(scale, panOffset.x, panOffset.y, rotationAngle, cropBoxSizePx)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Gunakan Foto", fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

private fun loadAndSampleBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        null
    }
}

private fun performCrop(
    bitmap: Bitmap,
    scale: Float,
    panX: Float,
    panY: Float,
    rotation: Float,
    cropBoxPx: Float
): Bitmap? {
    return try {
        val outputSize = 512
        val outputBitmap = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(outputBitmap)

        val matrix = Matrix()

        val bmpW = bitmap.width.toFloat()
        val bmpH = bitmap.height.toFloat()
        val baseScale = maxOf(cropBoxPx / bmpW, cropBoxPx / bmpH)
        val finalScale = baseScale * scale * (outputSize.toFloat() / cropBoxPx)

        // Geser ke tengah output
        matrix.postTranslate(-bmpW / 2f, -bmpH / 2f)
        matrix.postRotate(rotation)
        matrix.postScale(finalScale, finalScale)
        val outPanX = panX * (outputSize.toFloat() / cropBoxPx)
        val outPanY = panY * (outputSize.toFloat() / cropBoxPx)
        matrix.postTranslate(outputSize / 2f + outPanX, outputSize / 2f + outPanY)

        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(bitmap, matrix, paint)

        outputBitmap
    } catch (e: Exception) {
        null
    }
}

