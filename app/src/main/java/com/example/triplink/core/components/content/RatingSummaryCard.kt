package com.example.triplink.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplink.R
import com.example.triplink.ui.theme.PrincipalOrange
import java.util.Locale

@Composable
fun RatingSummaryCard(
    average: Double,
    totalReviews: Int,
    distribution: List<RatingCount>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format(Locale.ROOT, "%.1f", average),
                    color = PrincipalOrange,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 56.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (index < average.toInt()) PrincipalOrange else Color(0xFFD8DCE3)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.component_rating_summary_card_based_on),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF788A9F)
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.component_rating_summary_card_reviews_count,
                        totalReviews,
                        totalReviews
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF465A73)
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .height(120.dp)
                    .width(1.dp),
                color = Color(0xFFDADFE8)
            )

            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                distribution.forEach { item ->
                    RatingDistributionRow(item = item, totalReviews = totalReviews)
                }
            }
        }
    }
}

@Composable
private fun RatingDistributionRow(
    item: RatingCount,
    totalReviews: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalReviews > 0) item.count / totalReviews.toFloat() else 0f

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${item.stars}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF4D5F78),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(3.dp))
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = PrincipalOrange
        )
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(color = Color(0xFFE1E5EC), shape = RoundedCornerShape(999.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .background(color = PrincipalOrange, shape = RoundedCornerShape(999.dp))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF7D8EA4),
            fontWeight = FontWeight.Medium
        )
    }
}

data class RatingCount(
    val stars: Int,
    val count: Int
)

@Preview(showBackground = true)
@Composable
private fun RatingSummaryCardPreview() {
    RatingSummaryCard(
        average = 4.8,
        totalReviews = 128,
        distribution = listOf(
            RatingCount(stars = 5, count = 95),
            RatingCount(stars = 4, count = 22),
            RatingCount(stars = 3, count = 6),
            RatingCount(stars = 2, count = 3),
            RatingCount(stars = 1, count = 2)
        ),
        modifier = Modifier.padding(16.dp)
    )
}


