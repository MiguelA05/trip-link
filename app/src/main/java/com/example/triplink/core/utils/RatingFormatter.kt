package com.example.triplink.core.utils

import java.util.Locale

fun Double.toRatingLabel(): String = String.format(Locale.ROOT, "%.1f", this)

