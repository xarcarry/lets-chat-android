package xar.com.qqapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.internal.Streams;

import org.apache.commons.codec.binary.BaseNCodec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import xar.com.qqapp.bean.Image;
import xar.com.qqapp.utils.Base64Util;
import xar.com.qqapp.utils.OkHttpUtil;

public class MainActivity extends AppCompatActivity {

    private ImageView liveShow;
    private JSONArray images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        liveShow = findViewById(R.id.liveShow);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        fragmentTransaction.add(R.id.fragment_container, loginFragment);
        //fragmentTransaction.hide(loginFragment);
        fragmentTransaction.commit();

        images = new JSONArray();
        //liveShowStart();

    }

    public void liveShowStart(){
        getImagesInterval(2000);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                try {
                    if (images.size() >= 32){
                        showImagesInterval();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        long delay = 0;
        long intevalPeriod = 200;
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }

    public void getImagesInterval(int intevalPeriod){
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                try {
                    liveShow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        long delay = 0;
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }

    public void liveShow(){
        OkHttpUtil.getObject("show", callBack());
    }

    public Callback callBack(){
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray jsonArray = (JSONArray) JSONObject.parse(response.body().string());
                    if (!images.contains((String) jsonArray.get(0))) {
                        images.addAll(jsonArray);
                    }
                    jsonArray = null;
                    Log.e("wlf", "当前图库规模为：" + images.size() + "张图片！");
                }catch (Exception e){
                    Log.e("wlf", e.getMessage());
                }
            }
        };
    }

    public void showImagesInterval(){
        JSONArray jsonArray1 = new JSONArray();
        for (int i = 0; i < 32; i++){
            jsonArray1.add(images.get(0));
            images.remove(0);
        }
        final JSONArray jsonArray = jsonArray1;
        jsonArray1 = null;
        TimerTask task = new TimerTask() {
            private int count = 0;
            @Override
            public void run() {
                try {
                    if (count == 32){
                        jsonArray.clear();
                        return;
                    }
                    showImages(jsonArray, count);
                    count++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Timer timer = new Timer();
        long delay = 0;
        long intevalPeriod = 1 * 125;
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }
    public void showImages(JSONArray jsonArray, int count){
        byte[] bitmapArray;
        String str = (String) jsonArray.get(count);
        bitmapArray = android.util.Base64.decode(str, android.util.Base64.DEFAULT);
        final Bitmap bitmap=BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                liveShow.setImageBitmap(bitmap);//显示图片
            }
        });
        //bitmap.recycle();

    }
}
