package com.example.triplink.core.components.publicationdetails.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.triplink.R
import com.example.triplink.ui.theme.TextTokens

data class DayScheduleUi(
    val day: String,
    val hours: String,
    val isClosed: Boolean = false
)

@Composable
fun PublicationWeeklyScheduleSection(
    schedules: List<DayScheduleUi>,
    today: String,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    val sectionTitle = title ?: stringResource(R.string.component_publication_weekly_schedule_title)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = sectionTitle,
            style = TextTokens.sectionTitle(),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.size(12.dp))

        Surface(
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
            ) {
                schedules.forEach { item ->
                    val isToday = item.day == today

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isToday) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isToday) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                            } else {
                                Spacer(modifier = Modifier.size(14.dp))
                            }

                            Text(
                                text = item.day,
                                color = if (isToday) MaterialTheme.colorScheme.primary else if (item.isClosed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                style = TextTokens.emphasized(TextTokens.title(), FontWeight.Bold)
                            )

                            if (isToday) {
                                Spacer(modifier = Modifier.size(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.component_publication_weekly_schedule_today),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = TextTokens.emphasized(TextTokens.caption(), FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        Text(
                            text = item.hours,
                            color = if (isToday) MaterialTheme.colorScheme.primary else if (item.isClosed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = TextTokens.bodySecondary()
                        )
                    }
                }
            }
        }
    }
}

