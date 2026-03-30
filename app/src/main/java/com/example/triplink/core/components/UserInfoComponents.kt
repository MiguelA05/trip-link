package com.example.triplink.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplink.features.userInfo.ContributionTab
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun UserInfoHeader(
    userName: String,
    initials: String,
    roleLabel: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = PrincipalBlue,
                shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.background(Color.White.copy(alpha = 0.12f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = "Salir",
                    tint = PrincipalWhite
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.background(Color.White.copy(alpha = 0.12f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar perfil",
                    tint = PrincipalWhite
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(118.dp)
                    .background(Color(0xFFE8EBF1), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8796AA)
                    )
                )
            }

            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = PrincipalWhite,
                    fontWeight = FontWeight.Bold
                )
            )

            Surface(
                color = Color(0xFFF3AA17),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EmojiEvents,
                        contentDescription = null,
                        tint = PrincipalWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = roleLabel,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = PrincipalWhite,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun UserStatsRow(
    points: Int,
    contributions: Int,
    activeDays: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        UserStatItem(value = points, label = "PUNTOS", modifier = Modifier.weight(1f))
        VerticalSeparator()
        UserStatItem(value = contributions, label = "APORTES", modifier = Modifier.weight(1f))
        VerticalSeparator()
        UserStatItem(value = activeDays, label = "DIAS ACTIVOS", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun UserStatItem(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color(0xFF1B1B1B),
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color(0xFF90A0B7),
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun VerticalSeparator() {
    Box(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 2.dp)
            .background(Color(0xFFD5DCE8))
            .size(width = 1.dp, height = 48.dp)
    )
}

@Composable
fun UserInfoSectionCard(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F4F7)),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = actionLabel,
                    color = PrincipalBlue,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clickable(onClick = onActionClick)
                )
            }

            content()
        }
    }
}

@Composable
fun EmptyInfoState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Badge,
            contentDescription = null,
            tint = Color(0xFFD2D7E0),
            modifier = Modifier.size(44.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF8FA1BA))
        )
    }
}

@Composable
fun ContributionTabs(
    selectedTab: ContributionTab,
    onTabSelected: (ContributionTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        ContributionTab.VERIFIED to "Verificados",
        ContributionTab.PENDING to "Pendientes",
        ContributionTab.REJECTED to "Rechazados"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEach { (tab, label) ->
            val selected = selectedTab == tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (selected) PrincipalBlue else Color(0xFF8FA1BA),
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (selected) {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth(0.9f),
                        thickness = 3.dp,
                        color = PrincipalBlue
                    )
                }
            }
        }
    }
}

