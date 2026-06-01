package com.axel.mba.bpvpn.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

/**
 * Premium Minimalist Power Toggle Button for Black Panther VPN.
 * Features a circular chassis, subtle outer status ring, and iconic power glyph.
 */
class LiquidShieldButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isConnected: Boolean = false
    private var isConnecting: Boolean = false
    private var pulseAlpha: Float = 0.5f

    private var pulseAnimator: ValueAnimator? = null

    // Chassis background
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#12131C")
    }

    // Outer halo / aura ring
    private val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }

    // Main button border
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    // Power icon paint (Arc and vertical line)
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val powerArcRect = RectF()

    private val scaleSpring = SpringAnimation(this, SpringAnimation.SCALE_X, 1f).apply {
        spring = SpringForce(1f).apply {
            dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
            stiffness = SpringForce.STIFFNESS_LOW
        }
        addUpdateListener { _, value, _ ->
            scaleY = value
        }
    }

    init {
        setupPulseAnimation()
    }

    private fun setupPulseAnimation() {
        pulseAnimator = ValueAnimator.ofFloat(0.3f, 0.9f).apply {
            duration = 1000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                pulseAlpha = anim.animatedValue as Float
                if (isConnecting) {
                    invalidate()
                }
            }
        }
    }

    fun setState(connected: Boolean, connecting: Boolean) {
        this.isConnected = connected
        this.isConnecting = connecting

        if (connecting) {
            if (pulseAnimator?.isRunning != true) {
                pulseAnimator?.start()
            }
        } else {
            pulseAnimator?.cancel()
            pulseAlpha = 0.5f
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val radius = (Math.min(width, height) / 2f) - 20f

        val activeColor = Color.parseColor("#10B981") // Emerald Green
        val connectingColor = Color.parseColor("#F59E0B") // Amber
        val inactiveColor = Color.parseColor("#64677A") // Sleek Slate Grey
        val inactiveBorder = Color.parseColor("#262838")

        // 1. Outer Halo Ring
        when {
            isConnected -> {
                haloPaint.color = Color.argb(60, 16, 185, 129)
                canvas.drawCircle(cx, cy, radius + 12f, haloPaint)
            }
            isConnecting -> {
                haloPaint.color = Color.argb((pulseAlpha * 120).toInt(), 245, 158, 11)
                canvas.drawCircle(cx, cy, radius + 12f, haloPaint)
            }
            else -> {
                haloPaint.color = Color.parseColor("#181924")
                canvas.drawCircle(cx, cy, radius + 10f, haloPaint)
            }
        }

        // 2. Base Button Circle
        bgPaint.color = if (isConnected) Color.parseColor("#10181A") else Color.parseColor("#12131C")
        canvas.drawCircle(cx, cy, radius, bgPaint)

        // 3. Button Border
        when {
            isConnected -> {
                borderPaint.color = activeColor
                borderPaint.strokeWidth = 3.5f
            }
            isConnecting -> {
                borderPaint.color = connectingColor
                borderPaint.strokeWidth = 3.5f
            }
            else -> {
                borderPaint.color = inactiveBorder
                borderPaint.strokeWidth = 2.5f
            }
        }
        canvas.drawCircle(cx, cy, radius, borderPaint)

        // 4. Center Iconic Power Glyph
        val iconColor = when {
            isConnected -> activeColor
            isConnecting -> connectingColor
            else -> Color.parseColor("#E4E6F2")
        }

        val strokeW = radius * 0.12f
        iconPaint.color = iconColor
        iconPaint.strokeWidth = strokeW

        // Power circle arc: 260 degrees with opening at top (from 140 deg to 40 deg)
        val arcRadius = radius * 0.44f
        powerArcRect.set(cx - arcRadius, cy - arcRadius + 4f, cx + arcRadius, cy + arcRadius + 4f)
        canvas.drawArc(powerArcRect, 140f, 260f, false, iconPaint)

        // Vertical line through top opening
        val lineTop = cy - arcRadius - (strokeW * 0.3f) + 4f
        val lineBottom = cy - (arcRadius * 0.1f) + 4f
        canvas.drawLine(cx, lineTop, cx, lineBottom, iconPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleSpring.animateToFinalPosition(0.92f)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                scaleSpring.animateToFinalPosition(1f)
                if (event.action == MotionEvent.ACTION_UP) {
                    performClick()
                }
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pulseAnimator?.cancel()
    }
}
