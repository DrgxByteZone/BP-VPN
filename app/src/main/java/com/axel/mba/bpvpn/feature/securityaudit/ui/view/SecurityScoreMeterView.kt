package com.axel.mba.bpvpn.feature.securityaudit.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * 2D Minimalist Circular Security Score Meter View.
 * Strict flat minimalist aesthetic: no 3D bevels, no cheap gradients.
 * Created by: Axel & M.B.A
 */
class SecurityScoreMeterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentScore: Int = 100
    private var ratingText: String = "Sempurna"

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        color = Color.parseColor("#1C1E2B")
    }

    private val scorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        color = Color.parseColor("#00D26A")
        strokeCap = Paint.Cap.ROUND
    }

    private val textScorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F4F4F7")
        textSize = 64f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val textRatingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#85889A")
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    private val bounds = RectF()

    fun setScore(score: Int, rating: String) {
        this.currentScore = score.coerceIn(0, 100)
        this.ratingText = rating

        scorePaint.color = when {
            currentScore >= 80 -> Color.parseColor("#00D26A") // Emerald
            currentScore >= 50 -> Color.parseColor("#FFBB00") // Amber
            else -> Color.parseColor("#FF4D4D") // Crimson
        }

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 24f
        val diameter = (w.coerceAtMost(h) - padding * 2)
        val left = (w - diameter) / 2f
        val top = (h - diameter) / 2f
        bounds.set(left, top, left + diameter, top + diameter)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Full Track Circle
        canvas.drawArc(bounds, 0f, 360f, false, trackPaint)

        // 2. Score Arc
        val sweepAngle = (currentScore / 100f) * 360f
        if (sweepAngle > 0) {
            canvas.drawArc(bounds, -90f, sweepAngle, false, scorePaint)
        }

        // 3. Center Score & Rating
        val centerX = bounds.centerX()
        val centerY = bounds.centerY()

        canvas.drawText("$currentScore", centerX, centerY + 10f, textScorePaint)
        canvas.drawText(ratingText, centerX, centerY + 46f, textRatingPaint)
    }
}
