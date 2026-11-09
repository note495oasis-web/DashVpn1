package io.nekohasekai.sagernet.widget

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.ktx.dp2px

class GradientRingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp2px(4).toFloat()
    }
    private val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dp2px(4).toFloat()
        maskFilter = BlurMaskFilter(dp2px(8).toFloat(), BlurMaskFilter.Blur.NORMAL)
        alpha = 90
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val radius = minOf(cx, cy) - dp2px(12)
        if (radius <= 0) return
        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.dashvpn_accent),
            ContextCompat.getColor(context, R.color.dashvpn_accent_end),
            ContextCompat.getColor(context, R.color.dashvpn_accent),
        )
        val shader = SweepGradient(cx, cy, colors, null)
        paint.shader = shader
        glow.shader = shader
        canvas.drawCircle(cx, cy, radius, glow)
        canvas.drawCircle(cx, cy, radius, paint)
    }
}