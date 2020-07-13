package com.finn.slidingtablayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
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
import kotlinx.android.synthetic.main.view_sliding_tab_layout2.view.*
import kotlinx.android.synthetic.main.view_sliding_tab_layout2.view.left_text
import kotlinx.android.synthetic.main.view_sliding_tab_layout2.view.mid_text
import kotlinx.android.synthetic.main.view_sliding_tab_layout2.view.right_text
import kotlin.math.roundToInt

class SlidingTabLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var subInventoryText: TextView
    private var selectedText: TextView
    private var tabPosition: Int = 0

    private var indicatorLeft = -1
    private var indicatorRight = -1
    private var indicatorAnimator: ValueAnimator? = null

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
            if (subInventoryText != left_text) {
                subInventoryText = left_text
                animateIndicatorToPosition()
                toggleTabView()
            }
        }

        mid_text.setOnClickListener {
            if (subInventoryText != mid_text) {
                subInventoryText = mid_text
                animateIndicatorToPosition()
                toggleTabView()
            }
        }

        right_text.setOnClickListener {
            if (subInventoryText != right_text) {
                subInventoryText = right_text
                animateIndicatorToPosition()
                toggleTabView()
            }
        }
    }

    private fun toggleTabView() {
        if (subInventoryText != selectedText) {
            subInventoryText.setTextColor(resources.getColor(R.color.colorWhite))
            selectedText.setTextColor(resources.getColor(R.color.colorBlack))
            selectedText = subInventoryText
        }
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
                setIndicatorPosition(
                    lerpValue(startLeft, targetLeft, fraction),
                    lerpValue(startRight, targetRight, fraction)
                )
            }
            animator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animator: Animator) {
                    //ignore here
                }
            })
            animator?.start()
        }
    }

    private fun lerpValue(startValue: Int, endValue: Int, fraction: Float): Int {
        return startValue + (fraction * (endValue - startValue)).roundToInt()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        //更新显示tab位置
        updateSelectedTab()
    }

    //可设置显示默认位置
    fun setSelectedTab(tabPosition: Int = 0) {
        this.tabPosition = tabPosition
    }
    //设置显示位置
    fun updateSelectedTab(position: Int = -1) {
        if (position > -1 && tabPosition != position) {
            this.tabPosition = position
        }
        when (tabPosition) {
            0 -> {
                subInventoryText = left_text
            }
            1 -> {
                subInventoryText = mid_text
            }
            2 -> {
                subInventoryText = right_text
            }
            else -> {
            }
        }
        if (position == -1){
            viewIndicator.left = subInventoryText?.left
            viewIndicator.right = subInventoryText?.right
        }else{
            animateIndicatorToPosition()
        }
        toggleTabView()
    }

}
