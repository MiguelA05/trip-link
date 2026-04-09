package com.example.triplink.features.admin.reports.AdminReportDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.triplink.core.components.DestructiveConfirmDialog
import com.example.triplink.core.components.GeneralAlertDialog
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalGreen

@Composable
fun AdminReportDetailsScreen(
    reportId: String,
    contentPadding: PaddingValues = PaddingValues(),
    onBackClick: () -> Unit = {}
) {
    val viewModel: AdminReportsDetailsViewModel = hiltViewModel()
    val report = viewModel.getReportById(reportId)

    if (report == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F5F7))
                .padding(contentPadding)
                .statusBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Reporte no encontrado",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2430)
            )
        }
        return
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(contentPadding)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF1E2430)
                )
            }
            Text(
                text = "Detalle del Reporte",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2430)
            )
            Spacer(modifier = Modifier.size(48.dp))
        }

        // Imagen de la publicación
        if (report.imageUrl.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(report.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = report.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Información de la Publicación
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Información de la Publicación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2430)
                    )

                    Text(
                        text = report.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2430)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFE9D6)
                        ) {
                            Text(
                                text = report.categoryLabel,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFF07A17),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF9AA3B2),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = report.cityLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6A7688)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = PrincipalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = report.priceLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = PrincipalBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF8A93A3),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = report.scheduleLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF677487)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del Reporte
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Detalles del Reporte",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E2430)
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFF2F2)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Motivo del Reporte",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFFD84343),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = report.reasonMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1E2430)
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Reportado por",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF8A93A3),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = report.authorName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1E2430),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Fecha del Reporte",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF8A93A3),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = report.timeLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1E2430)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Reportes Aceptados",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF8A93A3),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${report.acceptedReportsCount} / 3",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1E2430),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF35A5A)
                ) {
                    Button(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "Rechazar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = PrincipalGreen
                ) {
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "Confirmar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Diálogos de confirmación
    if (showConfirmDialog) {
        GeneralAlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            onConfirm = {
                viewModel.confirmReport(report.id)
                onBackClick()
            },
            title = "Confirmar Reporte",
            message = "¿Deseas confirmar el reporte de «${report.title}»? Esta acción incrementará el contador de reportes aceptados.",
            icon = Icons.Default.CheckCircle,
            buttonText = "Confirmar",
            dismissButtonText = "Cancelar",
            onDismissAction = { showConfirmDialog = false }
        )
    }

    if (showRejectDialog) {
        DestructiveConfirmDialog(
            title = "Rechazar Reporte",
            message = "¿Deseas rechazar el reporte de «${report.title}»? Se considerará que la publicación no incumple las reglas.",
            confirmText = "Rechazar",
            dismissText = "Cancelar",
            onDismissRequest = { showRejectDialog = false },
            onConfirm = {
                viewModel.invalidateReport(report.id)
                onBackClick()
            }
        )
    }
}

