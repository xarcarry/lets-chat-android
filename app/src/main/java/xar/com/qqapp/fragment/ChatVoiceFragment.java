package xar.com.qqapp.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import xar.com.qqapp.ChatActivity;
import xar.com.qqapp.R;
import xar.com.qqapp.bean.Message;
import xar.com.qqapp.utils.AudioRecoderUtils;
import xar.com.qqapp.utils.OkHttpUtil;
import xar.com.qqapp.utils.TimeUtil;
import xar.com.qqapp.utils.ToastUtil;
import xar.com.qqapp.utils.code.ErrorCodeUtil;
import xar.com.qqapp.utils.code.MsgType;
import xar.com.qqapp.utils.voice.VoicePlayUtil;
import xar.com.qqapp.view.CircleImageView;

public class ChatVoiceFragment extends ChatToolsFragment {

    private CircleImageView mButton;
    private ImageView mImageView;
    private TextView mTextView;
    private AudioRecoderUtils mAudioRecoderUtils;
    private TextView recordSend;

    private boolean isShow;

    public ChatVoiceFragment(){
        this.isShow = true;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater
            , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_voice_fragment, container, false);


        //当前布局文件的根layout
        //final RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        mButton = v.findViewById(R.id.recordVoice);
        mImageView = v.findViewById(R.id.recordImageView);
        mTextView = v.findViewById(R.id.recordTextView);
        recordSend = v.findViewById(R.id.recordSend);
       // recordSen

        mAudioRecoderUtils = new AudioRecoderUtils();
        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {
            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                //根据分贝值来设置录音时话筒图标的上下波动，下面有讲解
                //mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(TimeUtil.longTimeToString(time));
            }
            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {
                Toast.makeText(v.getContext(), "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
                mTextView.setText(0 + "");
            }
        });
        //Button的touch监听
        mButton.setOnTouchListener(new View.OnTouchListener() {

            private float offsetX, offsetY;
            private float lastPosX, lastPosY;
           // private File file = new File(Environment.getExternalStorageDirectory() + "/record/currentRecord.amr");
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        offsetX = 0.0F;
                        offsetY = 0.0F;
                        lastPosX = event.getX();
                        lastPosY = event.getY();
                        if (!mAudioRecoderUtils.isRecording()) {
                            mTextView.setText("上滑取消，录音时长：0s");
                            mAudioRecoderUtils.startRecord();
                        }
                        /*if (file.exists()){
                            file.delete();
                        }*/
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!mAudioRecoderUtils.isCanceled()){
                            mAudioRecoderUtils.stopRecord();  //结束录音（保存录音文件）
                            File file = new File(Environment.getExternalStorageDirectory() + "/record/currentRecord.amr");
                            if (!file.exists()){
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            BufferedInputStream bis = null;
                            ByteArrayOutputStream bos = null;
                            try {
                                bis = new BufferedInputStream(new FileInputStream(file));
                                bos = new ByteArrayOutputStream();
                                byte[] temp = new byte[1024];
                                int len = 0;
                                while((len = bis.read(temp)) != -1){
                                    bos.write(temp);
                                }
                                byte[] voice = bos.toByteArray();
                                Message message = new Message(voice, MsgType.MSG_VOICE);
                                JSONObject json = (JSONObject)JSONObject.toJSON(message);
                                String jsonStr = json.toJSONString();
                                String backUrl = "chat/sendMsg";
                                OkHttpUtil.postObject(jsonStr, backUrl, handleVoiceResponse(v));
                                file.delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                try {
                                    bis.close();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            if (!mAudioRecoderUtils.isCanceled())
                                mAudioRecoderUtils.cancelRecord();
                        }

//      mAudioRecoderUtils.cancelRecord(); //取消录音（不保存录音文件）
                        mTextView.setText("按住说话");
                        break;
                        default:
                            float thisPosX = event.getX();
                            float thisPosY = event.getY();
                            offsetX += Math.abs(thisPosX - lastPosX);//x轴偏差
                            offsetY += Math.abs(thisPosY - lastPosY);//y轴偏差
                            lastPosX = thisPosX;
                            lastPosY = thisPosY;
                            if (offsetX < 30 && offsetY < 30){

                            }else if (offsetX < offsetY){
                                Log.e("wlf", "上滑了！");
                                if (!mAudioRecoderUtils.isCanceled())
                                    mAudioRecoderUtils.cancelRecord();
                            }
                            break;
                }
                return true;
            }

        });

        return v;
    }

    public Callback handleVoiceResponse(View v){
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseMsg = Integer.parseInt(response.body().string());
                if (responseMsg == ErrorCodeUtil.VOICE_SEND_FAILED){
                    ToastUtil.toast(v, "语音发送失败，请重试！", Toast.LENGTH_SHORT);
                }
            }
        };
    }
}
