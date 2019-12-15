package xar.com.qqapp.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import xar.com.qqapp.LoginFragment;

public class ToastUtil {

    public static void toast(View v, String text, int lengthTime){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(v.getContext(), text, lengthTime).show();
            }
        });
    }
}
