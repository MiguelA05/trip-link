package com.example.triplink.core.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EmojiEvents
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
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalWhite

@Composable
fun ProfileHeader(
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
            .statusBarsPadding()
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

