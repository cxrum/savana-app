package com.savana.ui.view.welcome_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.savana.R

class AnimatedBlobView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var circleRadius: Float = 30f
    private var circleFillColor: Int = Color.BLUE

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        loadAttributes(attrs)
        paint.color = circleFillColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        canvas.drawCircle(centerX, centerY, circleRadius, paint)
    }

    private fun loadAttributes(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.AnimatedBlobView, 0, 0) {
                circleFillColor = getColor(R.styleable.AnimatedBlobView_color, Color.BLUE)
                circleRadius = getDimension(R.styleable.AnimatedBlobView_radius, 30f)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (circleRadius * 2).toInt() + paddingLeft + paddingRight
        val desiredHeight = (circleRadius * 2).toInt() + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    fun setCircleRadius(radius: Float) {
        this.circleRadius = radius
        requestLayout()
        invalidate()
    }

    fun getCircleRadius(): Float {
        return this.circleRadius
    }

    fun setCircleFillColor(color: Int) {
        this.circleFillColor = color
        paint.color = this.circleFillColor
        invalidate()
    }

    fun getCircleFillColor(): Int {
        return this.circleFillColor
    }
}