package com.example.triplink.core.components.publicationdetails.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.triplink.R
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.PrincipalGray
import com.example.triplink.ui.theme.TextTokens

@Composable
fun PublicationPriceRangeSection(
    selectedLevel: String,
    modifier: Modifier = Modifier
) {
    val levels = listOf(
        stringResource(R.string.component_publication_price_range_free),
        stringResource(R.string.component_publication_price_range_economic),
        stringResource(R.string.component_publication_price_range_moderate),
        stringResource(R.string.component_publication_price_range_expensive)
    )
    val selectedIndex = levels.indexOf(selectedLevel).coerceAtLeast(0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.component_publication_price_range_title),
            style = TextTokens.sectionTitle(),
            color = Color(0xFF121826)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB)),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriceTag(text = "$", isSelected = selectedIndex == 0)
                    PriceTag(text = "$$", isSelected = selectedIndex == 1)
                    PriceTag(text = "$$$", isSelected = selectedIndex == 2)
                    PriceTag(text = "$$$$", isSelected = selectedIndex == 3)
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = selectedLevel,
                        color = PrincipalBlue,
                        style = TextTokens.cardTitle()
                    )
                    Text(
                        text = stringResource(R.string.component_publication_price_range_estimated),
                        color = PrincipalGray,
                        style = TextTokens.helperText()
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceTag(text: String, isSelected: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PrincipalBlue else Color(0xFFF1F5F9),
        modifier = Modifier.size(width = 50.dp, height = 45.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) Color.White else PrincipalGray,
                style = TextTokens.cardTitle()
            )
        }
    }
}

