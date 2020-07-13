package com.finn.slidingtablayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlinx.android.synthetic.main.header_inventory_category.view.*


@RequiresApi(Build.VERSION_CODES.M)
class CountCategoryHeader(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    private var subInventoryText: TextView
    private var selectedText: TextView

    private var mSelectedIndicatorHeight: Int = 0
    private val mSelectedIndicatorPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            setShadowLayer(5F, 3F, 3F, Color.DKGRAY)
        }
    }

    internal var mSelectionOffset: Float = 0.toFloat()

    private var mLayoutDirection = -1

    private var mIndicatorLeft = -1
    private var mIndicatorRight = -1

    private var mIndicatorAnimator: ValueAnimator? = null

    private val DEFAULT_DURATION = 200 // dps
    internal val MOTION_NON_ADJACENT_OFFSET = 24

    private val indicatorRect: RectF = RectF()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        Log.i("Tab-dispatchDraw", ">>>>>>${indicatorRect.left}>>>>>>${indicatorRect.right}")
        indicatorRect.set(
            mIndicatorLeft.toFloat(), (height - 50.toDp()),
            mIndicatorRight.toFloat(), height.toFloat()
        )
        if (mIndicatorLeft in 0 until mIndicatorRight) {
//            canvas.drawBitmap(indicatorRect, mSelectedIndicatorPaint)
            canvas.drawRoundRect(indicatorRect,
                MOTION_NON_ADJACENT_OFFSET.toDp(),
                MOTION_NON_ADJACENT_OFFSET.toDp(),
                mSelectedIndicatorPaint)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        Log.i("Tab-draw", ">>>>>>${mIndicatorLeft}>>>>>>${mIndicatorRight}")
    }


    internal fun setIndicatorPosition(left: Int, right: Int) {
        Log.i("Tab-setIndicator", ">>>>>>${left}>>>>>>${right}")

        if (left != mIndicatorLeft || right != mIndicatorRight) {
            // If the indicator's left/right hasBackStack changed, invalidate
            mIndicatorLeft = left
            mIndicatorRight = right
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    internal fun animateIndicatorToPosition() {
        if (mIndicatorAnimator != null && mIndicatorAnimator!!.isRunning) {
            mIndicatorAnimator!!.cancel()
        }
        val isRtl = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL

        val targetView = subInventoryText

        val targetLeft = targetView?.left
        val targetRight = targetView?.right
        val startLeft: Int = selectedText?.left ?: 0
        val startRight: Int = selectedText?.right ?: 0

        if (startLeft != targetLeft || startRight != targetRight) {
            mIndicatorAnimator = ValueAnimator()
            val animator = mIndicatorAnimator
            animator?.interpolator = FastOutSlowInInterpolator()
            animator?.duration = DEFAULT_DURATION.toLong()
            animator?.setFloatValues(0F, 100F)
            animator?.addUpdateListener { animator1 ->
                val fraction = animator1.animatedFraction
                Log.i("Tab-animatedFraction", ">>>>>>${animator1.animatedFraction}")
                setIndicatorPosition(
                    lerp(startLeft, targetLeft, fraction),
                    lerp(startRight, targetRight, fraction)
                )
            }
            animator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator) {
                    selectedText = subInventoryText
                    mSelectionOffset = 0f
                }
            })
            animator?.start()
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.i("Tab-onLayout", ">>>>>>${mIndicatorLeft}>>>>>>${mIndicatorRight}")
        mIndicatorLeft = left_text.left
        mIndicatorRight = left_text.right
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.i("Tab-onMeasure", ">>>>>>${mIndicatorLeft}>>>>>>${mIndicatorRight}")

    }

    init {
        LayoutInflater.from(context).inflate(R.layout.header_inventory_category, this, true)

        mSelectedIndicatorPaint.color = context.getColor(R.color.colorPrimary)
        selectedText = left_text
        subInventoryText = left_text

        left_text.setOnClickListener {
                subInventoryText = left_text
                animateIndicatorToPosition()
        }

        mid_text.setOnClickListener {
                subInventoryText = mid_text
                animateIndicatorToPosition()
        }

        right_text.setOnClickListener {
                subInventoryText = right_text
                animateIndicatorToPosition()

        }
    }

    private fun lerp(startValue: Int, endValue: Int, fraction: Float): Int {
        return startValue + Math.round(fraction * (endValue - startValue))
    }
}
