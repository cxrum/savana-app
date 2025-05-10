package com.savana.ui.view.sinus_wave

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.savana.R
import kotlin.math.PI
import kotlin.math.sin
import androidx.core.content.withStyledAttributes

class SineWaveLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val wavePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    private val wavePath: Path = Path()

    private var amplitude: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var phase: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var direction: Int = 1
        set(value) {
            field = value
            invalidate()
        }

    private var coef: Float = 0.1f
        set(value) {
            field = value
            invalidate()
        }



    private var waveLength: Float = 0f
    private var frequency: Float = 0.05f

    var minAmplitude: Float = 30f
    var maxAmplitude: Float = 150f

    private lateinit var phaseAnimator: ValueAnimator
    private lateinit var amplitudeAnimator: ValueAnimator

    private var waveCenterY: Float = 0f

    init {
        loadAttributes(attrs)
        setupAnimators()
    }

    private fun setupAnimators() {
        phaseAnimator = ValueAnimator.ofFloat(0f, 2 * PI.toFloat()).apply {
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            duration = 1500
            addUpdateListener { animation ->
                phase = animation.animatedValue as Float
            }
        }

        setupAmplitudeAnimator()
    }

    private fun setupAmplitudeAnimator() {
        val targetAmplitude = if (direction == 1) {
            maxAmplitude
        } else {
            minAmplitude
        }

        if (::amplitudeAnimator.isInitialized && amplitudeAnimator.isRunning) {
            amplitudeAnimator.cancel()
        }

        amplitudeAnimator = ValueAnimator.ofFloat(this.amplitude, targetAmplitude).apply {
            duration = 2000

            interpolator = LinearInterpolator()

            addUpdateListener { animation ->
                this@SineWaveLoadingView.amplitude = animation.animatedValue as Float
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)

                    if (isAttachedToWindow) {
                        direction *= -1
                        setupAmplitudeAnimator()
                        amplitudeAnimator.start()
                    }
                }
            })
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        waveLength = w.toFloat()
        waveCenterY = h / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.TRANSPARENT)

        wavePath.reset()
        wavePath.moveTo(0f, waveCenterY)

        val stepX = 1f
        var x = 0f
        while (x < width) {
            val y = sinusFunction(x)
            wavePath.lineTo(x, y.toFloat())
            x += stepX
        }

        canvas.drawPath(wavePath, wavePaint)
    }

    private fun sinusFunction(x: Float): Float {
        val amplitudeEnvelopeFactor = if (width > 0) {
            sin(PI.toFloat() * x / width.toFloat())
        } else {
            0f
        }

        val currentXAmplitude = amplitude * amplitudeEnvelopeFactor.coerceAtLeast(0f)

        return waveCenterY + currentXAmplitude * sin(frequency * x + phase)
    }

    fun startAnimation() {
        if (!phaseAnimator.isRunning) {
            phaseAnimator.start()
        }
        if (!amplitudeAnimator.isRunning) {
            setupAmplitudeAnimator()
            amplitudeAnimator.start()
        }
    }

    fun stopAnimation() {
        if (phaseAnimator.isRunning) {
            phaseAnimator.cancel()
        }
        if (amplitudeAnimator.isRunning) {
            amplitudeAnimator.cancel()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

     private fun loadAttributes(attrs: AttributeSet?) {
         attrs?.let {
             context.withStyledAttributes(it, R.styleable.SineWaveLoadingView, 0, 0) {
                 wavePaint.color = getColor(R.styleable.SineWaveLoadingView_waveColor, Color.BLUE)
                 wavePaint.strokeWidth =
                     getDimension(R.styleable.SineWaveLoadingView_waveStrokeWidth, 8f)
                 minAmplitude = getFloat(R.styleable.SineWaveLoadingView_minAmplitude, 30f)
                 maxAmplitude = getFloat(R.styleable.SineWaveLoadingView_maxAmplitude, 150f)
                 frequency = getFloat(R.styleable.SineWaveLoadingView_waveFrequency, 0.05f)
             }
         }
     }
}