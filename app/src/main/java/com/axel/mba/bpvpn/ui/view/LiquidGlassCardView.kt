package com.axel.mba.bpvpn.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

/**
 * Clean Minimalist Glass Card View for Black Panther VPN.
 * Provides refined contrast, crisp 1px borders, and tactile touch feedback.
 */
class LiquidGlassCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#131520")
    }

    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.parseColor("#25283B")
    }

    private val rect = RectF()
    private val cornerRadius = 40f

    private val scaleXSpring = SpringAnimation(this, SpringAnimation.SCALE_X, 1f).apply {
        spring = SpringForce(1f).apply {
            dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
            stiffness = SpringForce.STIFFNESS_LOW
        }
    }

    private val scaleYSpring = SpringAnimation(this, SpringAnimation.SCALE_Y, 1f).apply {
        spring = SpringForce(1f).apply {
            dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
            stiffness = SpringForce.STIFFNESS_LOW
        }
    }

    init {
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(2f, 2f, w.toFloat() - 2f, h.toFloat() - 2f)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isClickable && !isFocusable) return super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleXSpring.animateToFinalPosition(0.97f)
                scaleYSpring.animateToFinalPosition(0.97f)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                scaleXSpring.animateToFinalPosition(1f)
                scaleYSpring.animateToFinalPosition(1f)
            }
        }
        return super.onTouchEvent(event)
    }
}
