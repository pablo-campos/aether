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
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(12f, 3f)
            arcTo(9f, 9f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 21f)
            arcTo(9f, 9f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 3f)
        }.path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(5.64f, 5.64f)
            lineTo(18.36f, 18.36f)
            moveTo(18.36f, 5.64f)
            lineTo(5.64f, 18.36f)
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
            fill = SolidColor(Color.Black),
        ) {
            // Circle — clockwise winding
            moveTo(12f, 2f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 22f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 2f)
            // X — counterclockwise winding cuts through the circle
            moveTo(19.78f, 18.36f)
            lineTo(13.41f, 12f)
            lineTo(19.78f, 5.64f)
            lineTo(18.36f, 4.22f)
            lineTo(12f, 10.59f)
            lineTo(5.64f, 4.22f)
            lineTo(4.22f, 5.64f)
            lineTo(10.59f, 12f)
            lineTo(4.22f, 18.36f)
            lineTo(5.64f, 19.78f)
            lineTo(12f, 13.41f)
            lineTo(18.36f, 19.78f)
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
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(12f, 3f)
            arcTo(9f, 9f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 21f)
            arcTo(9f, 9f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 3f)
        }.path(
            fill = SolidColor(Color.Black),
        ) {
            moveTo(12f, 10f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 14f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 10f)
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
            fill = SolidColor(Color.Black),
        ) {
            moveTo(12f, 2f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 22f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 2f)

            moveTo(12f, 9f)
            arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 15f)
            arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 9f)
        }.build()
    }
}
