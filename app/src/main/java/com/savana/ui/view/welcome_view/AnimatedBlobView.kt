package com.savana.ui.view.welcome_view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import com.savana.R
import com.savana.core.extension.pxToDp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random

class AnimatedBlobView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var minRadius: Float = 30f
    private var maxRadius: Float = 30f
    private var circleFillColor: Int = Color.BLUE

    private var pathDefinitionCenterX: Float = 0f
    private var pathDefinitionCenterY: Float = 0f

    var currentDisplayCenterX: Float = 0f
        private set
    var currentDisplayCenterY: Float = 0f
        private set

    private var isPathInitialized = false

    private var vertexNum = 7
    private var changeRate = 20f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.MAGENTA
        style = Paint.Style.FILL
    }

    private val debugPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private val controlPointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }


    private val blobPath = Path()
    private val vertices = mutableListOf<BlobVertex>()

    private var showDebugPoints = false

    init {
        loadAttributes(attrs)
        paint.color = circleFillColor

        setupInitialVertices(vertexNum, minRadius)

        startAnimation()
    }

    private fun loadAttributes(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.AnimatedBlobView, 0, 0) {
                circleFillColor = getColor(R.styleable.AnimatedBlobView_color, Color.BLUE)
                minRadius = getDimension(R.styleable.AnimatedBlobView_radiusMin, 30f)
                maxRadius = getDimension(R.styleable.AnimatedBlobView_radiusMax, 30f)
                changeRate = getFloat(R.styleable.AnimatedBlobView_changeRange, 30f)
                vertexNum = getInteger(R.styleable.AnimatedBlobView_vertexNum, 7)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (maxRadius * 2).toInt() - (paddingLeft + paddingRight)
        val desiredHeight = (maxRadius * 2).toInt() - (paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        pathDefinitionCenterX = w / 2f
        pathDefinitionCenterY = h / 2f

        if (!isPathInitialized) {

            currentDisplayCenterX = pathDefinitionCenterX
            currentDisplayCenterY = pathDefinitionCenterY
            isPathInitialized = true
        }

        if (vertices.isEmpty() || oldw == 0 || oldh == 0) {
            setupInitialVertices(
                numVertices = vertexNum,
                baseRadius = minRadius,
            )
        } else {
            updatePath()
        }
    }

    private fun setupInitialVertices(numVertices: Int, baseRadius: Float) {
        vertices.clear()
        if (numVertices < 2) return

        if (width == 0 || height == 0) return

        this.pathDefinitionCenterX = width / 2f
        this.pathDefinitionCenterY = height / 2f

        val centerX = this.pathDefinitionCenterX
        val centerY = this.pathDefinitionCenterY

        val angleStep = (2 * PI / numVertices).toFloat()

        for (i in 0 until numVertices) {
            val angle = i * angleStep

            val random = Random.nextInt()%changeRate

            val ax = centerX + (baseRadius+random) * cos(angle)
            val ay = centerY + (baseRadius+random) * sin(angle)

            vertices.add(
                BlobVertex(
                    anchorX = ax, anchorY = ay,
                    controlOutX = ax, controlOutY = ay,
                    controlInX = ax, controlInY = ay,
                    increaseDirection = Random.nextBoolean()
                )
            )
        }

        val kFactor = (4.0f / 3.0f) * tan(angleStep / 4.0f)
        val controlArmLength = baseRadius * kFactor

        for (i in 0 until numVertices) {
            val currentVertex = vertices[i]
            val currentAngle = i * angleStep

            currentVertex.controlOutX =  currentVertex.anchorX - controlArmLength * sin(currentAngle)
            currentVertex.controlOutY = currentVertex.anchorY + controlArmLength * cos(currentAngle)

            currentVertex.controlInX = currentVertex.anchorX + controlArmLength * sin(currentAngle)
            currentVertex.controlInY = currentVertex.anchorY - controlArmLength * cos(currentAngle)
        }

        setPathDisplayCenter(centerX, centerY)
        updatePath()
    }

    private fun calculateSmoothControlPoints(tension: Float = 0.4f) {
        if (vertices.size < 2) return

        val n = vertices.size
        for (i in 0 until n) {
            val vPrev = vertices[(i - 1 + n) % n]
            val vCurr = vertices[i]
            val vNext = vertices[(i + 1) % n]
            val vNextNext = vertices[(i + 2) % n]

            vCurr.controlOutX = vCurr.anchorX + (vNext.anchorX - vPrev.anchorX) * tension / 2f
            vCurr.controlOutY = vCurr.anchorY + (vNext.anchorY - vPrev.anchorY) * tension / 2f

            vNext.controlInX = vNext.anchorX - (vNextNext.anchorX - vCurr.anchorX) * tension / 2f
            vNext.controlInY = vNext.anchorY - (vNextNext.anchorY - vCurr.anchorY) * tension / 2f
        }
    }

    private fun updatePath() {
        if (vertices.size < 2) return

        blobPath.reset()
        blobPath.moveTo(vertices[0].anchorX, vertices[0].anchorY)

        for (i in 0 until vertices.size) {
            val currentVertex = vertices[i]
            val nextVertex = vertices[(i + 1) % vertices.size]

            blobPath.cubicTo(
                currentVertex.controlOutX, currentVertex.controlOutY,
                nextVertex.controlInX, nextVertex.controlInY,
                nextVertex.anchorX, nextVertex.anchorY
            )
        }
        blobPath.close()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val translateX = currentDisplayCenterX - pathDefinitionCenterX
        val translateY = currentDisplayCenterY - pathDefinitionCenterY

        canvas.save()
        canvas.translate(translateX, translateY)

        canvas.drawPath(blobPath, paint)

        if (showDebugPoints) {
            vertices.forEach { v ->
                canvas.drawCircle(v.anchorX, v.anchorY, 15f, debugPaint)

                canvas.drawCircle(v.controlInX, v.controlInY, 8f, controlPointPaint)
                canvas.drawCircle(v.controlOutX, v.controlOutY, 8f, controlPointPaint)

                canvas.drawLine(v.anchorX, v.anchorY, v.controlInX, v.controlInY, controlPointPaint)
                canvas.drawLine(v.anchorX, v.anchorY, v.controlOutX, v.controlOutY, controlPointPaint)
            }
        }
        canvas.restore()
    }

    fun setPathDisplayCenter(newCenterX: Float, newCenterY: Float) {
        currentDisplayCenterX = newCenterX
        currentDisplayCenterY = newCenterY
        invalidate()
    }

    private var animator: ValueAnimator? = null
    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 10000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()

            addUpdateListener { animation ->

                if (width == 0 || height == 0) return@addUpdateListener

                val centerX = width / 2f
                val centerY = height / 2f

                val animationMaxRadius = maxRadius - changeRate
                val animationMinRadius = minRadius

                vertices.forEach { vertex ->

                    val factor = animation.animatedFraction
                    val stepAmount = vertex.speed * factor

                    val dxToCenter = vertex.anchorX - centerX
                    val dyToCenter = vertex.anchorY - centerY
                    var currentDistance = sqrt(dxToCenter * dxToCenter + dyToCenter * dyToCenter)

                    val dirX = if (currentDistance == 0f) 0f else dxToCenter / currentDistance
                    val dirY = if (currentDistance == 0f) 0f else dyToCenter / currentDistance

                    if (!vertex.increaseDirection) {
                        vertex.anchorX += dirX * stepAmount
                        vertex.anchorY += dirY * stepAmount
                    } else {
                        vertex.anchorX -= dirX * stepAmount
                        vertex.anchorY -= dirY * stepAmount
                    }

                    val newDx = vertex.anchorX - centerX
                    val newDy = vertex.anchorY - centerY
                    currentDistance = sqrt(newDx * newDx + newDy * newDy)

                    if (!vertex.increaseDirection && currentDistance >= animationMaxRadius) {
                        vertex.increaseDirection = true

                        val overshoot = currentDistance - animationMaxRadius
                        vertex.anchorX -= dirX * overshoot
                        vertex.anchorY -= dirY * overshoot

                    } else if (vertex.increaseDirection && currentDistance <= animationMinRadius) {
                        vertex.increaseDirection = false

                        val undershoot = animationMinRadius - currentDistance
                        vertex.anchorX -= dirX * undershoot
                        vertex.anchorY -= dirY * undershoot
                    }
                }
                calculateSmoothControlPoints()
                updatePath()
            }
            start()
        }
    }


    fun stopAnimation() {
        animator?.cancel()
    }

    data class BlobVertex(
        var anchorX: Float,
        var anchorY: Float,

        var controlOutX: Float,
        var controlOutY: Float,

        var controlInX: Float,
        var controlInY: Float,
        var cornerRadius: Float = 0f,

        var increaseDirection: Boolean = false,
        var speed: Float = 0.5f
    )
}