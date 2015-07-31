package com.toc.dlpush.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.toc.dlpush.SlidingMenu;

/**
 * 本事例是在网上DOWN下来的，开始的时候这个事例listview的上下滑动和滑动功能栏的左右滑动有冲突。
 * 本人对其做了一些修改，并且添加了一些注释。希望可以帮到一些朋友。有问题欢迎发帖留言。
 * @author 你啊你
 * @date 2013-11-28
 */

public class CustomLinerLayout extends LinearLayout {

    private GestureDetector mGestureDetector;
    private boolean b;// 拦截touch标识

    public CustomLinerLayout(Context context) {

        super(context);
    }

    public CustomLinerLayout(Context context, AttributeSet attrs) {

        super(context, attrs);
        mGestureDetector = new GestureDetector(new MySimpleGesture());
    }

    /**
     * dispatchTouchEvent分发事件分析：
     * 如果 return true，事件会分发给当前 View 并由 dispatchTouchEvent 方法进行消费，同时事件会停止向下传递；
     * 如果 return false，事件分发分为两种情况：
     * 1)如果当前 View 获取的事件直接来自 Activity，则会将事件返回给 Activity 的 onTouchEvent 进行消费；
     * 2)如果当前 View 获取的事件来自外层父控件，则会将事件返回给父 View 的  onTouchEvent 进行消费。
     * 如果返回系统默认的 super.dispatchTouchEvent(ev)，事件会自动的分发给当前 View 的 onInterceptTouchEvent 方法。
     *
     * 在本方法中我们返回了系统默认的super.dispatchTouchEvent(ev)。
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        b = mGestureDetector.onTouchEvent(ev);// 获取手势返回值.
        System.out.println("CustomLinerLayout-dispatchTouchEvent==" + b);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 1）如果 onInterceptTouchEvent 返回 true：
     * 则表示将事件进行拦截，并将拦截到的事件交由当前 View 的 onTouchEvent 进行处理；
     * 2）如果 onInterceptTouchEvent 返回 false：
     * 则表示将事件放行，当前 View 上的事件会被传递到子 View 上，再由子 View 的 dispatchTouchEvent 来开始这个事件的分发；
     * 3）如果 onInterceptTouchEvent 返回 super.onInterceptTouchEvent(ev)：
     * 事件默认会被拦截，并将拦截到的事件交由当前 View 的 onTouchEvent 进行处理。
     *
     * 在该方法中我们返回的是手势返回值。主要是为了解决listview和左右滑动冲突的问题，返回值由下面的手势自定义方法获得。
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return b;
    }

    /**
     *
     * 1)如果事件传递到当前 View 的 onTouchEvent 方法，而该方法返回了 false，
     * 那么这个事件会从当前 View 向上传递，并且都是由上层 View 的 onTouchEvent 来接收，如果传递到上面的 onTouchEvent 也返回 false，这个事件就会“消失”，而且接收不到下一次事件。
     * 2)如果返回了 true 则会接收并消费该事件。
     * 3)如果返回 super.onTouchEvent(ev) 默认处理事件的逻辑和返回 false 时相同。
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    /**
     * 自定义手势监听
     */
    class MySimpleGesture extends SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            if (SlidingMenu.isOpen) {

                // 向下传递
                return true;
            } else {

                return super.onDown(e);
            }
        }

        /**
         * 手指在触摸屏上滑动。
         * @return true  表示事件被消耗
         * @return false 表示事件没有被消耗
         */

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.i("MotionEvent","e1 "+e1.getX()+" e2 "+e2.getX());
            // 垂直滑动距离大于水平会动距离

            if (Math.abs(distanceY) > ((Math.abs(distanceX)))) {
                Log.i("MotionEvent","false");
                return false;
            } else {
                System.out.println(e1.getX() + "!!!" + e2.getX());
                // 判断从右往左还是从左往右滑动
                int lastX = (int) e1.getX();//手指按下的位置
                int currentX = (int) e2.getX();//手指抬起的位置
                if (currentX - lastX > 0) {
                    Log.i("MotionEvent","true");
                    //判断从左向右滑动→
                    return true;
                } else {
                    Log.i("MotionEvent","false");
                    return false;
                }
            }
        }
    }
}
