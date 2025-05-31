package com.savana.ui.view.songplayer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.savana.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SongGapSelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private var totalDurationSec: Int = 100
    private var currentSec: Int = 0
    private var gapDurationSec: Int = 30

    var gapDurationSecDefault: Int = 30
        private set

    var currentStartSec: Int = 0
        private set
    var currentEndSec: Int = 0
        private set

    private var trackHeight: Float = 10f.dpToPx()
    private var inactiveTrackColor: Int = Color.LTGRAY
    private var activeTrackColor: Int = Color.RED
    private var trackCornerRadius: Float = 5f.dpToPx()

    private var thumbColor: Int = Color.BLACK
    private var thumbRadius: Float = 8f.dpToPx()
    private var thumbStrokeColor: Int = Color.TRANSPARENT
    private var thumbStrokeWidth: Float = 0f

    private var textColor: Int = Color.BLACK
    private var textMargin: Float = 16f
    private var textSize: Float = 14f.spToPx()
    private var showTotalDurationText: Boolean = true
    private var showSelectedRangeText: Boolean = true

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private var activeThumb: Thumb? = null
    private var touchDownX: Float = 0f
    private var touchDownStartSec: Int = 0
    private var touchDownEndSec: Int = 0

    private enum class Thumb { START, END, RANGE  }

    private val startThumbRect = RectF()
    private val endThumbRect = RectF()
    private val draggableActiveTrackRect = RectF()

    private var onRangeChangedListener: ((startSec: Int, endSec: Int) -> Unit)? = null

    init {
        loadAttributes(attrs, defStyleAttr)
        setupPaints()

        currentStartSec = 0
        currentEndSec = min(gapDurationSec, totalDurationSec)

        setWillNotDraw(false)
    }

    private fun loadAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.SongGapSelectorView, defStyleAttr, 0) {
                totalDurationSec = getInteger(R.styleable.SongGapSelectorView_sgs_totalDuration, totalDurationSec)
                gapDurationSec = getInteger(R.styleable.SongGapSelectorView_sgs_gapDuration, gapDurationSec)

                trackHeight = getDimension(R.styleable.SongGapSelectorView_sgs_trackHeight, trackHeight)
                inactiveTrackColor = getColor(R.styleable.SongGapSelectorView_sgs_inactiveTrackColor, inactiveTrackColor)
                activeTrackColor = getColor(R.styleable.SongGapSelectorView_sgs_activeTrackColor, activeTrackColor)
                trackCornerRadius = getDimension(R.styleable.SongGapSelectorView_sgs_trackCornerRadius, trackCornerRadius)

                thumbColor = getColor(R.styleable.SongGapSelectorView_sgs_thumbColor, thumbColor)
                thumbRadius = getDimension(R.styleable.SongGapSelectorView_sgs_thumbRadius, thumbRadius)
                thumbStrokeColor = getColor(R.styleable.SongGapSelectorView_sgs_thumbStrokeColor, thumbStrokeColor)
                thumbStrokeWidth = getDimension(R.styleable.SongGapSelectorView_sgs_thumbStrokeWidth, thumbStrokeWidth)

                textColor = getColor(R.styleable.SongGapSelectorView_sgs_textColor, textColor)
                textSize = getDimension(R.styleable.SongGapSelectorView_sgs_textSize, textSize)
                textMargin = getDimension(R.styleable.SongGapSelectorView_sgs_innerTextMargin, textMargin)

                showTotalDurationText = getBoolean(R.styleable.SongGapSelectorView_sgs_showTotalDurationText, showTotalDurationText)
                showSelectedRangeText = getBoolean(R.styleable.SongGapSelectorView_sgs_showSelectedRangeText, showSelectedRangeText)
            }
        }

        gapDurationSecDefault = gapDurationSec

        gapDurationSec = min(gapDurationSec, totalDurationSec)
    }

    private fun setupPaints() {
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize
        textPaint.color = textColor
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val paddingStart = thumbRadius + textPaint.textSize/2
        val paddingEnd = thumbRadius + textPaint.textSize/2
        val drawableWidth = viewWidth - paddingStart - paddingEnd

        val trackY = viewHeight / 2f

        trackPaint.color = inactiveTrackColor
        val inactiveTrackRect = RectF(paddingStart, trackY - trackHeight / 2, paddingStart + drawableWidth, trackY + trackHeight / 2)
        canvas.drawRoundRect(inactiveTrackRect, trackCornerRadius, trackCornerRadius, trackPaint)

        if (totalDurationSec > 0) {
            val startPixel = paddingStart + (currentStartSec.toFloat() / totalDurationSec) * drawableWidth
            val endPixel = paddingStart + (currentEndSec.toFloat() / totalDurationSec) * drawableWidth

            trackPaint.color = activeTrackColor
            val activeTrackRect = RectF(startPixel, trackY - trackHeight / 2, endPixel, trackY + trackHeight / 2)

            canvas.drawRoundRect(activeTrackRect, trackCornerRadius, trackCornerRadius, trackPaint)

            val draggableTrackVerticalPadding = trackHeight * 0.5f
            draggableActiveTrackRect.set(
                activeTrackRect.left,
                activeTrackRect.top - draggableTrackVerticalPadding,
                activeTrackRect.right,
                activeTrackRect.bottom + draggableTrackVerticalPadding
            )

            thumbPaint.color = thumbColor
            thumbPaint.style = Paint.Style.FILL
            canvas.drawCircle(startPixel, trackY, thumbRadius, thumbPaint)
            startThumbRect.set(startPixel - thumbRadius, trackY - thumbRadius, startPixel + thumbRadius, trackY + thumbRadius)

            canvas.drawCircle(endPixel, trackY, thumbRadius, thumbPaint)
            endThumbRect.set(endPixel - thumbRadius, trackY - thumbRadius, endPixel + thumbRadius, trackY + thumbRadius)

            if (thumbStrokeWidth > 0f) {
                thumbPaint.color = thumbStrokeColor
                thumbPaint.style = Paint.Style.STROKE
                thumbPaint.strokeWidth = thumbStrokeWidth
                canvas.drawCircle(startPixel, trackY, thumbRadius, thumbPaint)
                canvas.drawCircle(endPixel, trackY, thumbRadius, thumbPaint)
            }

            textPaint.color = textColor
            val textY = trackY - thumbRadius - 8f.dpToPx()

            val defaultTextY = trackY - thumbRadius - 8f.dpToPx()
            val startTextY = defaultTextY
            var endTextY = defaultTextY

            val startTimeText = formatTime(currentStartSec)
            val endTimeText = formatTime(currentEndSec)

            if (showSelectedRangeText) {
                val startTimeTextWidth = textPaint.measureText(startTimeText)
                val endTimeTextWidth = textPaint.measureText(endTimeText)

                val startTimeTextRightEdge = startPixel + startTimeTextWidth / 2
                val endTimeTextLeftEdge = endPixel - endTimeTextWidth / 2

                val textSpacingBuffer = 4f.dpToPx()

                if (startTimeTextRightEdge + textSpacingBuffer > endTimeTextLeftEdge) {
                    endTextY = trackY + thumbRadius + textPaint.textSize + 8f.dpToPx()
                }

                textPaint.textAlign = Paint.Align.CENTER
                canvas.drawText(startTimeText, startPixel, startTextY, textPaint)
                canvas.drawText(endTimeText, endPixel, endTextY, textPaint)
            }

            if (showTotalDurationText) {
                textPaint.textAlign = Paint.Align.RIGHT
                canvas.drawText(formatTime(totalDurationSec), viewWidth - textPaint.textSize - 4f.dpToPx(), trackY + textPaint.textSize + textMargin.dpToPx(), textPaint)
                textPaint.textAlign = Paint.Align.CENTER

                textPaint.textAlign = Paint.Align.LEFT
                canvas.drawText(formatTime(currentSec), textPaint.textSize + 4f.dpToPx(), trackY + textPaint.textSize + textMargin.dpToPx(), textPaint)
                textPaint.textAlign = Paint.Align.CENTER
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (totalDurationSec <= 0) return super.onTouchEvent(event)

        val x = event.x
        val y = event.y
        val paddingStart = thumbRadius + 10f.dpToPx()
        val paddingEnd = if (showTotalDurationText) 60f.dpToPx() else thumbRadius + 10f.dpToPx()
        val drawableWidth = width - paddingStart - paddingEnd

        if (drawableWidth <= 0) return super.onTouchEvent(event)


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                if (startThumbRect.contains(x, y)) {
                    activeThumb = Thumb.START
                    touchDownX = x
                    touchDownStartSec = currentStartSec
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                } else if (endThumbRect.contains(x, y)) {
                    activeThumb = Thumb.END
                    touchDownX = x
                    touchDownEndSec = currentEndSec
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                } else if (draggableActiveTrackRect.contains(x, y)) {
                    activeThumb = Thumb.RANGE
                    touchDownX = x
                    touchDownStartSec = currentStartSec
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                if (activeThumb != null) {
                    val dx = x - touchDownX
                    val deltaSeconds = (dx / drawableWidth * totalDurationSec).roundToInt()

                    var needsUpdate = false

                    when (activeThumb) {
                        Thumb.START -> {
                            var newStartSec = touchDownStartSec + deltaSeconds

                            newStartSec = max(0, min(newStartSec, totalDurationSec - gapDurationSec))

                            if (currentStartSec != newStartSec) {
                                currentStartSec = newStartSec
                                currentEndSec = currentStartSec + gapDurationSec

                                currentEndSec = min(currentEndSec, totalDurationSec)
                                needsUpdate = true
                            }
                        }
                        Thumb.END -> {
                            var newEndSec = touchDownEndSec + deltaSeconds

                            newEndSec = max(gapDurationSec, min(newEndSec, totalDurationSec))

                            if (currentEndSec != newEndSec) {
                                currentEndSec = newEndSec
                                currentStartSec = currentEndSec - gapDurationSec

                                currentStartSec = max(0, currentStartSec)
                                needsUpdate = true
                            }
                        }
                        Thumb.RANGE -> {
                            var newStartSec = touchDownStartSec + deltaSeconds

                            if (newStartSec < 0) {
                                newStartSec = 0
                            }
                            if (newStartSec + gapDurationSec > totalDurationSec) {
                                newStartSec = totalDurationSec - gapDurationSec
                            }

                            newStartSec = max(0, newStartSec)


                            if (currentStartSec != newStartSec) {
                                currentStartSec = newStartSec
                                currentEndSec = currentStartSec + gapDurationSec
                                needsUpdate = true
                            }
                        }
                        null -> {}
                    }

                    if (needsUpdate) {
                        invalidate()
                    }
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (activeThumb != null) {
                    activeThumb = null
                    onRangeChangedListener?.invoke(currentStartSec, currentEndSec)
                    parent.requestDisallowInterceptTouchEvent(false)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun setTotalDuration(seconds: Int) {
        if (seconds < 0) return
        totalDurationSec = seconds
        gapDurationSec = min(gapDurationSec, totalDurationSec)
        currentStartSec = min(currentStartSec, totalDurationSec - gapDurationSec)
        currentStartSec = max(0, currentStartSec)
        currentEndSec = currentStartSec + gapDurationSec
        currentEndSec = min(currentEndSec, totalDurationSec)
        setGapDuration(gapDurationSecDefault)
        invalidate()
    }

    fun setGapDuration(seconds: Int) {
        if (seconds <= 0 || seconds > totalDurationSec) return
        gapDurationSec = seconds
        currentEndSec = currentStartSec + gapDurationSec
        if (currentEndSec > totalDurationSec) {
            currentEndSec = totalDurationSec
            currentStartSec = currentEndSec - gapDurationSec
            currentStartSec = max(0, currentStartSec)
        }
        onRangeChangedListener?.invoke(currentStartSec, currentEndSec)
        invalidate()
    }

    private fun setCurrentStartPosition(startSeconds: Int) {
        if (totalDurationSec <= 0) return
        var newStart = max(0, min(startSeconds, totalDurationSec - gapDurationSec))
        if (newStart < 0) newStart = 0

        if (currentStartSec != newStart) {
            currentStartSec = newStart
            currentEndSec = currentStartSec + gapDurationSec

            if (currentEndSec > totalDurationSec) {
                currentEndSec = totalDurationSec
                currentStartSec = currentEndSec - gapDurationSec
                currentStartSec = max(0, currentStartSec)
            }
            onRangeChangedListener?.invoke(currentStartSec, currentEndSec)
            invalidate()
        }
    }

    fun getCurrentRange(): Pair<Int, Int> {
        return Pair(currentStartSec, currentEndSec)
    }

    fun onRangeChanged(action: (startSec: Int, endSec: Int) -> Unit){
        onRangeChangedListener = action
    }

    fun seekGapStartTo(startSec: Int){
        setCurrentStartPosition(startSec)
        onRangeChangedListener?.invoke(startSec, startSec+gapDurationSec)
    }

    fun setCurrentPlayedSeconds(seconds: Int){
        if (seconds < 0) return
        currentSec = seconds
        invalidate()
    }

    private fun Float.dpToPx(): Float = this * resources.displayMetrics.density
    private fun Float.spToPx(): Float = this * resources.displayMetrics.scaledDensity

    private fun formatTime(seconds: Int): String {
        if (seconds == 0) return "0:00"
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (thumbRadius * 2 + paddingTop + paddingBottom + textSize*2 + textMargin.dpToPx()).toInt()
        val height = resolveSize(desiredHeight, heightMeasureSpec)

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
    }
}