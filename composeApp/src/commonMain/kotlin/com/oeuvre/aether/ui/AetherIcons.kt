package com.oeuvre.aether.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object AetherIcons {
    val SeekOutlined: ImageVector by lazy {
        ImageVector.Builder(
            name = "SeekOutlined",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(7f, 5f)
            lineTo(19f, 12f)
            lineTo(7f, 19f)
            close()
        }.build()
    }

    val SeekFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "SeekFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).path(
            fill = SolidColor(Color.Black)
        ) {
            moveTo(7f, 5f)
            lineTo(19f, 12f)
            lineTo(7f, 19f)
            close()
        }.build()
    }

    val KeepOutlined: ImageVector by lazy {
        ImageVector.Builder(
            name = "KeepOutlined",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(5f, 5f)
            lineTo(15f, 5f)
            arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19f, 9f)
            lineTo(19f, 19f)
            lineTo(5f, 19f)
            close()
        }.build()
    }

    val KeepFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "KeepFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).path(
            fill = SolidColor(Color.Black)
        ) {
            moveTo(5f, 5f)
            lineTo(15f, 5f)
            arcTo(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19f, 9f)
            lineTo(19f, 19f)
            lineTo(5f, 19f)
            close()
        }.build()
    }
}
