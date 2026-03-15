package com.sanomush.tari.helper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var azimuth: Float = 0f

    // Paint untuk lingkaran luar
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#1A1A1A")
    }

    private val circleBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#FFEB38")
    }

    // Paint jarum merah (Utara)
    private val needleNorthPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#D32F2F")
    }

    // Paint jarum putih (Selatan)
    private val needleSouthPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFFFFF")
    }

    // Paint teks arah mata angin
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFEB38")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val textSecondaryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#AAAAAA")
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    // Paint titik tengah
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFEB38")
    }

    // Paint tick marks
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#555555")
        strokeWidth = 2f
    }

    private val tickMajorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#FFEB38")
        strokeWidth = 3f
    }

    fun setAzimuth(degrees: Float) {
        azimuth = degrees
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = min(cx, cy) - 10f

        textPaint.textSize = radius * 0.18f
        textSecondaryPaint.textSize = radius * 0.13f

        // Gambar lingkaran latar
        canvas.drawCircle(cx, cy, radius, circlePaint)
        canvas.drawCircle(cx, cy, radius, circleBorderPaint)

        // Gambar tick marks (setiap 10 derajat)
        canvas.save()
        canvas.rotate(-azimuth, cx, cy)
        for (i in 0 until 36) {
            val angle = Math.toRadians((i * 10).toDouble())
            val isMajor = i % 9 == 0 // setiap 90 derajat
            val tickStart = if (isMajor) radius * 0.78f else radius * 0.85f
            val tickEnd = radius * 0.93f
            val paint = if (isMajor) tickMajorPaint else tickPaint

            val startX = cx + tickStart * sin(angle).toFloat()
            val startY = cy - tickStart * cos(angle).toFloat()
            val endX = cx + tickEnd * sin(angle).toFloat()
            val endY = cy - tickEnd * cos(angle).toFloat()
            canvas.drawLine(startX, startY, endX, endY, paint)
        }

        // Gambar label arah mata angin
        val labelRadius = radius * 0.65f
        val directions = arrayOf("U", "T", "S", "B")
        val angles = arrayOf(0.0, 90.0, 180.0, 270.0)

        for (i in directions.indices) {
            val angle = Math.toRadians(angles[i])
            val x = cx + labelRadius * sin(angle).toFloat()
            val y = cy - labelRadius * cos(angle).toFloat() + textPaint.textSize / 3
            canvas.drawText(directions[i], x, y, textPaint)
        }

        canvas.restore()

        // Gambar jarum kompas (tidak ikut rotate — selalu menunjuk Utara)
        drawNeedle(canvas, cx, cy, radius)

        // Titik tengah
        canvas.drawCircle(cx, cy, radius * 0.06f, centerPaint)
        canvas.drawCircle(cx, cy, radius * 0.03f, circlePaint)
    }

    private fun drawNeedle(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val needleLength = radius * 0.55f
        val needleWidth = radius * 0.07f

        // Jarum Utara (Merah) — selalu ke atas layar
        val northPath = Path().apply {
            moveTo(cx, cy - needleLength)
            lineTo(cx - needleWidth, cy)
            lineTo(cx, cy - needleWidth * 0.5f)
            lineTo(cx + needleWidth, cy)
            close()
        }
        canvas.drawPath(northPath, needleNorthPaint)

        // Jarum Selatan (Putih) — selalu ke bawah
        val southPath = Path().apply {
            moveTo(cx, cy + needleLength)
            lineTo(cx - needleWidth, cy)
            lineTo(cx, cy + needleWidth * 0.5f)
            lineTo(cx + needleWidth, cy)
            close()
        }
        canvas.drawPath(southPath, needleSouthPaint)
    }
}