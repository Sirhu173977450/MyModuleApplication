package com.Parallax.effects;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 视差特效ListView
 * @author poplar
 *
 */
public class ParallaxListView extends ListView {

	private ImageView iv_header;
	private int orignalHeight;
	private int drawableHeight;
	public ParallaxListView(Context context) {
		super(context);
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setParallaxImage(ImageView iv_header) {
		this.iv_header = iv_header;

		// ImageView 初始高度
		orignalHeight = iv_header.getHeight();
		
		// 图片的原始高度
		drawableHeight = iv_header.getDrawable().getIntrinsicHeight();
		
	}
	/**
	 * 滑动到ListView两端才会被调用
	 */
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// deltaY 竖直方向滑动的瞬时变化量, 顶部下拉为- , 底部上拉为+
		// scrollY 竖直方向的滑动超出的距离, 顶部为-, 底部为+
		// scrollRangeY 竖直方向滑动的范围
		// maxOverScrollY 竖直方向最大的滑动位置
		// isTouchEvent 是否是用户触摸拉动 , true表示用户手指触摸拉动, false 是惯性
		
		System.out.println("deltaY: " + deltaY + " scrollY: " + scrollY
				+ " scrollRangeY: " + scrollRangeY + " maxOverScrollY: " + maxOverScrollY
				+ " isTouchEvent: " + isTouchEvent);
		
		// 顶部下拉, 用户触摸操作
		if(deltaY < 0 && isTouchEvent){
			// deltaY的绝对值, 累加给Header
			int newHeight = iv_header.getHeight() + Math.abs(deltaY / 3);
			if(newHeight <= drawableHeight){
				System.out.println("newHeight: " + newHeight);
				// 让新的值生效
				iv_header.getLayoutParams().height = newHeight;
				iv_header.requestLayout();
			}
			
		}
		
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			
			// 把当前的头布局的高度currentHeight恢复到初始高度orignalHeight
			final int currentHeight = iv_header.getHeight();
			
			// 300 -> 160
			ValueAnimator animator = ValueAnimator.ofInt(currentHeight, orignalHeight);
			// 动画更新的监听
			animator.addUpdateListener(new AnimatorUpdateListener() {
				
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// 0.0 -> 1.0
					// 获取动画执行过程中的分度值
					float fraction = animation.getAnimatedFraction();
//					Integer evaluate = evaluate(fraction, currentHeight, orignalHeight);
					
					// 获取中间的值
					Integer animatedValue = (Integer) animation.getAnimatedValue();
					System.out.println("fraction: " + fraction + " animatedValue: " + animatedValue);
					
					// evaluate == animatedValue

					// 让新的高度值生效
					iv_header.getLayoutParams().height = animatedValue;
					iv_header.requestLayout();
					
				}
			});
			animator.setInterpolator(new OvershootInterpolator(2));
			animator.setDuration(500);
			animator.start();
			
			break;

		default:
			break;
		}
		
		return super.onTouchEvent(ev);
	}
	
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }

}
