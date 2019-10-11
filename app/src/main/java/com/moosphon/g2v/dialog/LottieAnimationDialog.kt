package com.moosphon.g2v.dialog

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import com.airbnb.lottie.LottieAnimationView
import com.moosphon.g2v.R
import com.moosphon.g2v.util.applyViewGone
import org.jetbrains.anko.find

/**
 * @author moosphon
 * desc: Lottie动画的统一风格弹窗封装{@link:https://www.lottiefiles.com}
 */
class LottieAnimationDialog(context: Context, @StyleRes theme: Int) : Dialog(context, theme){

    private var canDisturbed = true                      //弹窗能否被中断

    private var animResource: String? = null             //lottie动画资源

    private var isLoop = false                           //动画是否循环播放

    private var animateView: LottieAnimationView? = null //目标动画视图

    private var animateText: TextView? = null            //动画提示文本

    private var tipContent: String = ""                  //文本提示内容

    private var mListener: OnLottieAnimationListener? = null

    constructor(context: Context, resource: String):this(context, R.style.DialogTransparent){
        this.animResource = resource

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_anim_lottie_layout)
        initView()
    }

    private fun initView(){
        animateView = find(R.id.dialog_lottie_animView)
        animateText = find(R.id.dialog_lottie_tip_text)
        animateView?.setAnimation(animResource)
        animateView?.loop(isLoop)
        setOnDismissListener {
            stopAnimation()
        }
        animateView?.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
                mListener?.onAnimationRepeat()
            }

            override fun onAnimationEnd(animation: Animator?) {
                mListener?.onAnimationEnd()
            }

            override fun onAnimationCancel(animation: Animator?) {
                mListener?.onAnimationCancel()
            }

            override fun onAnimationStart(animation: Animator?) {
                mListener?.onAnimationStart()
            }

        })
    }


    /***
     * 设置lottie动画资源路径
     * @param resource 动画资源路径
     */
    fun setAnimationResource(resource: String){
        this.animResource = resource
        animateView?.setAnimation(animResource)
    }


    fun setAnimationTipText(tip: String){
        this.tipContent = tip
        animateText?.text = tip
        //animateText?.applyVisible(true)
    }

    fun hideAnimationTipText() {
        animateText?.text = tipContent
        animateText?.applyViewGone(true)
    }

    fun setScale(factor: Float){
        animateView?.scale = factor
    }

    fun setTipTextColor(@ColorInt color: Int){
        animateText?.setTextColor(color)
    }

    fun isAnimationLoop(loop: Boolean){
        this.isLoop = loop
        animateView?.loop(isLoop)
    }

    fun isDialogCanDisturb(isDisturbed: Boolean){
        this.canDisturbed = isDisturbed
        if (this.canDisturbed){
            setCanceledOnTouchOutside(true)
            setCancelable(true)
        }else{
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
    }

    /***
     * 设置动画的执行进度
     * @param progress 进度
     */
    fun setAnimationProgress(progress: Float){
        if (isLoop){
            isLoop = false
        }
        animateView?.progress = progress
    }

    private fun playAnimation(){
        animateView?.playAnimation()
    }

    private fun stopAnimation(){
        if (animateView!=null && animateView?.isAnimating!!){
            animateView?.cancelAnimation()
        }
    }

    override fun show() {
        super.show()
        if (this.tipContent.isNotBlank()) {
            animateText?.text = tipContent
            animateText?.applyViewGone(false)
        }
    }

    fun startLoading() {
        playAnimation()
        show()
    }

    fun cancelLoading() {
        stopAnimation()
        dismiss()
    }

    fun setLottieAnimationListener(listener: OnLottieAnimationListener){
        this.mListener  =listener
    }

    interface OnLottieAnimationListener{
        fun onAnimationStart()
        fun onAnimationCancel()
        fun onAnimationRepeat()
        fun onAnimationEnd()
    }


}