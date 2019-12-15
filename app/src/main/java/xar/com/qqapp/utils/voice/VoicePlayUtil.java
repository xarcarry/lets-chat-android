package xar.com.qqapp.utils.voice;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import xar.com.qqapp.utils.TimeUtil;

public class VoicePlayUtil {

    private static String filePath = Environment.getExternalStorageDirectory()+"/QQApp/fileReceive/voice/";
    //当前是否正在播放
    private static volatile boolean isPlaying;
    //播放音频文件API
    private static MediaPlayer mediaPlayer;

    public static File createFile(byte[] byteAmr){
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(filePath);
            if (!file.exists()){
                file.mkdirs();
            }
            String fileName = TimeUtil.getCurrentTime();
            File receivedFile = new File(filePath +fileName + ".amr");
            /*if (!receivedFile.exists()){
                receivedFile.mkdir();
            }*/
            fos = new FileOutputStream(receivedFile);
            bos = new BufferedOutputStream(fos);
            bos.write(byteAmr);
            return receivedFile;
        } catch (IOException ex) {
            String string = ex.toString();
            Log.d("String", "string = " + string);
            ex.printStackTrace();
            return null;
        }finally {
            try {
                fos.close();
                bos.close();
            }catch (IOException e){
                Log.e("wlf", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void playAmr(File file) {
        FileInputStream fis = null;
        try {
            //播放音频文件
            MediaPlayer mediaPlayer = new MediaPlayer();
            fis = new FileInputStream(file);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ex) {
            String string = ex.toString();
            Log.d("String", "string = " + string);
            ex.printStackTrace();
        }finally {
            try {
                fis.close();
            }catch (IOException e){
                Log.e("wlf", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void startPlay(File mFile) {
        try {
            //初始化播放器
            MediaPlayer mediaPlayer = new MediaPlayer();
            //设置播放音频数据文件
            mediaPlayer.setDataSource(mFile.getAbsolutePath());
            //设置播放监听事件
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //播放完成
                    playEndOrFail(true);
                }
            });
            //播放发生错误监听事件
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    playEndOrFail(false);
                    return true;
                }
            });
            //播放器音量配置
            mediaPlayer.setVolume(1, 1);
            //是否循环播放
            mediaPlayer.setLooping(false);
            //准备及播放
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            //播放失败正理
            playEndOrFail(false);
        }

    }

    public static void playEndOrFail(boolean isEnd) {
        isPlaying = false;
        if (null != mediaPlayer) {
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
