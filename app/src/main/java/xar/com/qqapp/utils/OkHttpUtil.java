package xar.com.qqapp.utils;

import android.util.Log;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {

    // 1.拿到okhttpClient对象
    public static OkHttpClient okHttpClient = new OkHttpClient();
    public static String frontUrl ="http://192.168.43.22:8080/";

    public static void postObject(String message, String backUrl, Callback callback){
        //2.创建 RequestBody 设置提交类型MediaType+json字符串
        RequestBody requestBody =  RequestBody.create(MediaType.parse("application/json"), message);

        // 3.构造Request
        Request.Builder builder = new Request.Builder();

        StringBuffer url = new StringBuffer(OkHttpUtil.frontUrl);
        url.append(backUrl);

        Request request = builder.url(url.toString())
                .post(requestBody)//字符串
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void getObject(String backUrl, Callback callback){
        Request.Builder builder = new Request.Builder();

        StringBuffer url = new StringBuffer(OkHttpUtil.frontUrl);
        url.append(backUrl);

        Request request = builder.url(url.toString())
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }
}
