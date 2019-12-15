package xar.com.qqapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import xar.com.qqapp.R;

public class ViewPageAdapter extends PagerAdapter {

    private View[] listViews;
    private Context context;

    //构造方法
    public ViewPageAdapter(View[] listViews, Context context){
        this.listViews = listViews;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listViews.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0){
            return makeTabItemTitle(context, "消息", R.drawable.message_normal);
        }else if (position == 1){
            return makeTabItemTitle(context, "联系人", R.drawable.contact_normal);
        }else if (position == 2){
            return makeTabItemTitle(context, "动态", R.drawable.space_normal);
        }
        return null;
    }

    /*
    实例化一个子View，container是子View容器，就是ViewPager
    position是当前的页数，从0开始计
     */

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v = listViews[position];
        //必须加入容器中
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    public CharSequence makeTabItemTitle(Context context, String title, int iconResId){
        Drawable image = ContextCompat.getDrawable(context, iconResId);
        image.setBounds(0, 0, 40, 40);
        //Replace blank spaces with image icon.
        SpannableString ss = new SpannableString(" \n" + title);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BASELINE);
        ss.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

}
