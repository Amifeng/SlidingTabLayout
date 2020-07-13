package com.finn.slidingtablayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlinx.android.synthetic.main.view_sliding_tab_layout2.view.*
import kotlin.math.roundToInt

class SlidingTabLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var subInventoryText: TextView
    private var selectedText: TextView

    private var indicatorLeft = -1
    private var indicatorRight = -1
    private var indicatorAnimator: ValueAnimator? = null
    private var isIndicatorAnimatorEnd: Boolean = true

    companion object {
        private const val DEFAULT_TOGGLE_DURATION: Long = 300
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_sliding_tab_layout2, this, true)

        //init text
        selectedText = left_text
        subInventoryText = left_text
        selectedText.setTextColor(resources.getColor(R.color.colorWhite))

        left_text.setOnClickListener {
            if (isIndicatorAnimatorEnd && subInventoryText != left_text) {
                isIndicatorAnimatorEnd = false
                subInventoryText = left_text
                animateIndicatorToPosition()
                toggleTabView()
            }
        }

        mid_text.setOnClickListener {
            if (isIndicatorAnimatorEnd && subInventoryText != mid_text) {
                isIndicatorAnimatorEnd = false
                subInventoryText = mid_text
                animateIndicatorToPosition()
                toggleTabView()
            }
        }

        right_text.setOnClickListener {
            if (isIndicatorAnimatorEnd && subInventoryText != right_text) {
                isIndicatorAnimatorEnd = false
                subInventoryText = right_text
                animateIndicatorToPosition()
                toggleTabView()
            }
        }
    }

    private fun toggleTabView() {
        subInventoryText.setTextColor(resources.getColor(R.color.colorWhite))
        selectedText.setTextColor(resources.getColor(R.color.colorBlack))
    }

    private fun setIndicatorPosition(left: Int, right: Int) {
        if (left != indicatorLeft || right != indicatorRight) {
            // If the indicator's left/right hasBackStack changed, invalidate
            indicatorLeft = left
            indicatorRight = right
            viewIndicator.left = indicatorLeft
            viewIndicator.right = indicatorRight
        }
    }

    private fun animateIndicatorToPosition() {
        if (indicatorAnimator != null && indicatorAnimator!!.isRunning) {
            indicatorAnimator!!.cancel()
        }
        val targetLeft: Int = subInventoryText?.left
        val targetRight: Int = subInventoryText?.right
        val startLeft: Int = selectedText?.left
        val startRight: Int = selectedText?.right

        if (startLeft != targetLeft || startRight != targetRight) {
            indicatorAnimator = ValueAnimator()
            val animator = indicatorAnimator
            animator?.interpolator = FastOutSlowInInterpolator()
            animator?.duration = DEFAULT_TOGGLE_DURATION
            animator?.setFloatValues(0F, 100F)
            animator?.addUpdateListener { animator1 ->
                val fraction = animator1.animatedFraction
                Log.i("Tab-animatedFraction", ">>>>>>${animator1.animatedFraction}")
                setIndicatorPosition(
                    lerpValue(startLeft, targetLeft, fraction),
                    lerpValue(startRight, targetRight, fraction)
                )
            }
            animator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator) {
                    selectedText = subInventoryText
                    isIndicatorAnimatorEnd = true
                }
            })
            animator?.start()
        }
    }

    private fun lerpValue(startValue: Int, endValue: Int, fraction: Float): Int {
        return startValue + (fraction * (endValue - startValue)).roundToInt()
    }

}
