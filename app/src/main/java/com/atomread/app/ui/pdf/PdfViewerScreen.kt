package com.atomread.app.ui.pdf

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PdfViewerScreen(pdfUri: Uri) {
    val context = LocalContext.current
    var pdfBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Scaling state
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    // Load PDF from Uri (SAF compatible for API 29)
    LaunchedEffect(pdfUri) {
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(pdfUri)?.use { inputStream ->
                    val document = PDDocument.load(inputStream)
                    val renderer = PDFRenderer(document)
                    // Render first page at a reasonable DPI
                    val bitmap = renderer.renderImageWithDPI(0, 100f) 
                    pdfBitmap = bitmap
                    document.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = state),
        contentAlignment = Alignment.Center
    ) {
        pdfBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "PDF Page",
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        }
    }
}
