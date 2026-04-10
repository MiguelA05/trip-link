package com.example.triplink.core.components.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.triplink.R
import com.example.triplink.ui.theme.PastelBlue
import com.example.triplink.ui.theme.PrincipalBlue
import com.example.triplink.ui.theme.AppTitleVariant
import com.example.triplink.ui.theme.TextTokens

@Composable
fun AppTitle(
    modifier: Modifier = Modifier,
    variant: AppTitleVariant = AppTitleVariant.Standard
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(R.string.component_app_title_trip),
            style = TextTokens.appTitle(variant),
            color = PastelBlue
        )
        Text(
            text = stringResource(R.string.component_app_title_link),
            style = TextTokens.appTitle(variant),
            color = PrincipalBlue
        )
    }
}
