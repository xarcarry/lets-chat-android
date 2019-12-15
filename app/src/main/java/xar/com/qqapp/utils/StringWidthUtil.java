package xar.com.qqapp.utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import xar.com.qqapp.R;

public class StringWidthUtil {
    public static int getStringWidth(String str){
        Paint pFont = new Paint();
        Rect rect = new Rect();

//返回包围整个字符串的最小的一个Rect区域
        pFont.getTextBounds(str, 0, 1, rect);

        return rect.width();
    }
}
