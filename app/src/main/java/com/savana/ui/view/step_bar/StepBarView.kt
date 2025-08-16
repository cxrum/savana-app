package com.savana.ui.view.step_bar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.savana.R // Заміни на свій R-файл

class StepBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var currentStep: Int = 0
        private set

    var maxSteps: Int = 1
        private set

    private var stepSpacing: Float = 0f
    private var activeStepColor: Int = Color.DKGRAY
    private var inactiveStepColor: Int = Color.LTGRAY
    private var stepCornerRadius: Float = 0f
    private var stepHeight: Float = 0f

    private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val inactivePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stepRect = RectF()

    companion object {
        private const val DEFAULT_MAX_STEPS_ATTR = 4
        private const val DEFAULT_CURRENT_STEP_ATTR = 0
        private const val DEFAULT_SPACING_DP = 8f
        private val DEFAULT_ACTIVE_COLOR_ATTR = Color.parseColor("#FF424242")
        private val DEFAULT_INACTIVE_COLOR_ATTR = Color.parseColor("#FFD3D3D3")
        private const val DEFAULT_CORNER_RADIUS_DP = 8f
        private const val DEFAULT_STEP_HEIGHT_DP = 16f
    }

    init {
        val density = context.resources.displayMetrics.density

        stepSpacing = DEFAULT_SPACING_DP * density
        activeStepColor = DEFAULT_ACTIVE_COLOR_ATTR
        inactiveStepColor = DEFAULT_INACTIVE_COLOR_ATTR
        stepCornerRadius = DEFAULT_CORNER_RADIUS_DP * density
        stepHeight = DEFAULT_STEP_HEIGHT_DP * density

        loadAttributes(attrs, defStyleAttr)
        setupPaints()

        currentStep = currentStep.coerceIn(0, maxSteps)
    }

    private fun loadAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        context.withStyledAttributes(attrs, R.styleable.StepBarView, defStyleAttr, 0) {
            maxSteps = getInteger(R.styleable.StepBarView_maxStep, DEFAULT_MAX_STEPS_ATTR)
            currentStep = getInteger(R.styleable.StepBarView_currentStep, DEFAULT_CURRENT_STEP_ATTR)

            stepSpacing = getDimension(R.styleable.StepBarView_android_spacing, stepSpacing)
            activeStepColor = getColor(R.styleable.StepBarView_step_color, activeStepColor)
            stepCornerRadius = getDimension(R.styleable.StepBarView_step_corner, stepCornerRadius)

             inactiveStepColor = getColor(R.styleable.StepBarView_inactive_step_color, inactiveStepColor)
             stepHeight = getDimension(R.styleable.StepBarView_step_height, stepHeight)
        }
    }

    private fun setupPaints() {
        activePaint.style = Paint.Style.FILL
        activePaint.color = activeStepColor

        inactivePaint.style = Paint.Style.FILL
        inactivePaint.color = inactiveStepColor
    }

    fun nextStep() {
        if (currentStep < maxSteps) {
            currentStep++
            invalidate()
        }
    }

    fun previousStep() {
        if (currentStep > 0) {
            currentStep--
            invalidate()
        }
    }

    /**
     * Встановлює поточний крок програмно.
     * @param step Номер кроку (0-індексований, тобто від 0 до maxSteps-1 для активних кроків,
     * або кількість завершених кроків від 0 до maxSteps).
     */
    fun setCurrentStep(step: Int) {
        val newStep = step.coerceIn(0, maxSteps)
        if (newStep != currentStep) {
            currentStep = newStep
            invalidate()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = calculateDesiredWidth()
        val desiredHeight = (stepHeight + paddingTop + paddingBottom).toInt()

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun calculateDesiredWidth(): Int {
        if (maxSteps <= 0) {
            return paddingLeft + paddingRight
        }
        val estimatedStepItemWidth = stepHeight * 2
        val totalStepsWidth = estimatedStepItemWidth * maxSteps
        val totalSpacingWidth = stepSpacing * (maxSteps - 1).coerceAtLeast(0)
        return (totalStepsWidth + totalSpacingWidth + paddingLeft + paddingRight).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (maxSteps <= 0) {
            return
        }

        val drawableWidth = width - paddingLeft - paddingRight
        val actualStepHeight = stepHeight

        val totalSpacing = stepSpacing * (maxSteps - 1).coerceAtLeast(0)
        val singleStepWidth = (drawableWidth - totalSpacing) / maxSteps

        if (singleStepWidth <= 0) {
            return
        }

        var currentX = paddingLeft.toFloat()
        val stepTop = paddingTop.toFloat()
        val stepBottom = stepTop + actualStepHeight

        for (i in 0 until maxSteps) {
            stepRect.set(currentX, stepTop, currentX + singleStepWidth, stepBottom)

            val paintToUse = if (i < currentStep) activePaint else inactivePaint
            canvas.drawRoundRect(stepRect, stepCornerRadius, stepCornerRadius, paintToUse)

            currentX += singleStepWidth + stepSpacing
        }
    }
}