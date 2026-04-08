package com.example.triplink.features.notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.triplink.core.components.GeneralTopBar
import com.example.triplink.ui.theme.*



@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel()
) {


    Scaffold(
        topBar = {
            GeneralTopBar(
                title = "Notificaciones",
                onBack = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            if (viewModel.areNotificationsEmpty()) {
                EmptyNotificationsView()
            } else {
                NotificationsList(
                    notifications = viewModel.notifications,
                    onMarkAllAsRead = { viewModel.notifications = emptyList() },
                    onNotificationClick = { id ->
                        viewModel.notifications = viewModel.notifications.filter { it.id != id }
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyNotificationsView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsOff,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = PrincipalGray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No tienes notificaciones",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = DarkGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Te avisaremos cuando haya novedades importantes para ti",
            style = MaterialTheme.typography.bodyLarge,
            color = PrincipalGray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun NotificationsList(
    notifications: List<NotificationItem>,
    onMarkAllAsRead: () -> Unit,
    onNotificationClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onMarkAllAsRead,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = PrincipalBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Marcar todas como leídas",
                        style = MaterialTheme.typography.labelLarge,
                        color = PrincipalBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(notifications) { notification ->
            NotificationCard(
                notification = notification,
                onClick = { onNotificationClick(notification.id) }
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de ubicación en círculo azul suave
            Surface(
                shape = CircleShape,
                color = SoftBlue,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = PrincipalBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    // Punto azul de no leído
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(PrincipalBlue, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notification.time,
                    fontSize = 12.sp,
                    color = PrincipalGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
