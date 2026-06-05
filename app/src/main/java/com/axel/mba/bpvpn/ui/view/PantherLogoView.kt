package com.axel.mba.bpvpn.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * 2D Minimalist Geometric Black Panther Silhouette.
 * Strictly: No gradients, No 3D, No neon.
 */
class PantherLogoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#F4F4F7")
    }

    private val eyeCutoutPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#08080A")
    }

    private val path = Path()
    private val leftEyePath = Path()
    private val rightEyePath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val s = Math.min(w, h).toFloat()
        val cx = w / 2f
        val cy = h / 2f
        val scale = s / 96f

        path.reset()
        // Crown & Ears
        path.moveTo(cx + (-30f * scale), cy + (-26f * scale))
        path.lineTo(cx + (-16f * scale), cy + (-14f * scale))
        path.lineTo(cx + (-10f * scale), cy + (-28f * scale))
        path.lineTo(cx + (0f * scale), cy + (-22f * scale))
        path.lineTo(cx + (10f * scale), cy + (-28f * scale))
        path.lineTo(cx + (16f * scale), cy + (-14f * scale))
        path.lineTo(cx + (30f * scale), cy + (-26f * scale))
        // Temples & Cheeks
        path.lineTo(cx + (22f * scale), cy + (-4f * scale))
        path.lineTo(cx + (34f * scale), cy + (6f * scale))
        path.lineTo(cx + (18f * scale), cy + (14f * scale))
        path.lineTo(cx + (12f * scale), cy + (6f * scale))
        // Muzzle & Jaw
        path.lineTo(cx + (0f * scale), cy + (10f * scale))
        path.lineTo(cx + (-12f * scale), cy + (6f * scale))
        path.lineTo(cx + (-18f * scale), cy + (14f * scale))
        path.lineTo(cx + (-34f * scale), cy + (6f * scale))
        path.lineTo(cx + (-22f * scale), cy + (-4f * scale))
        path.close()

        // Chin extension
        path.moveTo(cx + (-6f * scale), cy + (14f * scale))
        path.lineTo(cx + (0f * scale), cy + (24f * scale))
        path.lineTo(cx + (6f * scale), cy + (14f * scale))
        path.close()

        // Geometric Eyes Cutout
        leftEyePath.reset()
        leftEyePath.moveTo(cx + (-16f * scale), cy + (-4f * scale))
        leftEyePath.lineTo(cx + (-8f * scale), cy + (-2f * scale))
        leftEyePath.lineTo(cx + (-14f * scale), cy + (2f * scale))
        leftEyePath.close()

        rightEyePath.reset()
        rightEyePath.moveTo(cx + (16f * scale), cy + (-4f * scale))
        rightEyePath.lineTo(cx + (8f * scale), cy + (-2f * scale))
        rightEyePath.lineTo(cx + (14f * scale), cy + (2f * scale))
        rightEyePath.close()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, fillPaint)
        canvas.drawPath(leftEyePath, eyeCutoutPaint)
        canvas.drawPath(rightEyePath, eyeCutoutPaint)
    }
}
