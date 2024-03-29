package xar.com.qqapp.viewPager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class QQViewPager extends ViewPager {
    //必须实现带一个参数的构造方法
    public QQViewPager(@NonNull Context context) {
        super(context);
    }
    //必须实现此构造方法，否则在界面设计器中不能正常显示
    public QQViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
