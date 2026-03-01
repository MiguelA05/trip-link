package com.example.triplink.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplink.core.components.GeneralButton
import com.example.triplink.R


@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.width(300.dp)
        )
        Text(
            text = "Aplicación de guía turistica",
            modifier = Modifier.padding(bottom = 40.dp),
            fontSize = 24.sp

        )

        GeneralButton(
            icon = Icons.Filled.Add,
            contentDescription = "Register icon",
            onClick = { },
            text = "Register"
        )


        GeneralButton(
            primary = false,
            icon = Icons.Filled.Person,
            contentDescription = "Login icon",
            onClick = { },
            text = "Login"
        )


    }

}