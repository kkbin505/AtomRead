package com.atomread.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atomread.app.ui.theme.AtomReadTheme
import com.atomread.app.ui.pdf.PdfViewerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(applicationContext)
        
        setContent {
            AtomReadTheme {
                var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    selectedPdfUri = uri
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (selectedPdfUri != null) {
                        PdfViewerScreen(selectedPdfUri!!)
                    } else {
                        // Lab Vibe Welcome Screen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.background,
                                            Color(0xFF001220)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "ATOM READ",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        letterSpacing = 8.sp,
                                        fontWeight = FontWeight.Light,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = "Molecular Thinking Reactor",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        letterSpacing = 2.sp,
                                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                    )
                                )
                                Spacer(modifier = Modifier.height(64.dp))
                                Button(
                                    onClick = { launcher.launch("application/pdf") },
                                    shape = RoundedCornerShape(2.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    ),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                    modifier = Modifier.height(48.dp).width(200.dp)
                                ) {
                                    Text("INITIATE EXTRACTION", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
