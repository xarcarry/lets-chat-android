package xar.com.qqapp;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import xar.com.qqapp.bean.Message;
import xar.com.qqapp.bean.MyApplication;
import xar.com.qqapp.fragment.ChatToolsFragment;
import xar.com.qqapp.fragment.ChatVoiceFragment;
import xar.com.qqapp.utils.OkHttpUtil;
import xar.com.qqapp.utils.WebSocketConnect;
import xar.com.qqapp.view.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageView chatIconVoice;
    private LinearLayout chatTools;
    private CircleImageView recordVoice;
    private ChatVoiceFragment chatVoiceFragment;
    private List<ChatToolsFragment> chatToolsFragment;

    //存放一条消息之类的数据
    public static class ChatMessage {
        String contactName;//联系人的名字
        long time;//日期
        String content;//消息的内容
        boolean isMe;//这个消息是不是我发出的？

        public ChatMessage(String contactName, long time, String content, boolean isMe) {
            this.contactName = contactName;
            this.time = time;
            this.content = content;
            this.isMe = isMe;
        }
    }

    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initChatTools();

        //获取聊天图标
        chatIconVoice = findViewById(R.id.chatToolsIcon2);
        chatTools = findViewById(R.id.chatTools);
        recordVoice = findViewById(R.id.recordVoice);

        LinearLayout linearLayout = findViewById(R.id.chatLinearLayout);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.colorChat));
        EditText editText = findViewById(R.id.editMessage);
        editText.setBackgroundColor(Color.WHITE);

        //设置动作栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //获取启动此Activity时传过来的数据
        //在启动聊天界面时，通过此方式把对方的名字传过来
        String contactName = getIntent().getStringExtra("contactName");
        long userId = getIntent().getLongExtra("contactId", 0);
        if (contactName != null && userId != 0) {
            toolbar.setTitle(contactName);
            //Toast.makeText(this, ""+userId, Toast.LENGTH_LONG).show();
        }

        setSupportActionBar(toolbar);
        //设置显示动作栏上的返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //获取Recycler控件并设置适配器
        final RecyclerView recyclerView = findViewById(R.id.chatMessageListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ChatMessageAdapter(this));

        //View.inflate(MyApplication.getContext(), R.layout.activity_chat, null);
        new WebSocketConnect(this, recyclerView).connectServer();

        findViewById(R.id.buttonSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.editMessage);
                String msg = editText.getText().toString();
                Message chatMessage = new Message("adroidUser", new Date().getTime(), msg, 1);
                //点击发送按钮后，清空发送按钮的内容
                editText.setText("");

                JSONObject jsonObject = (JSONObject) JSONObject.toJSON(chatMessage);
                Log.e("wlf", jsonObject.toJSONString());
                String backUrl = "chat/sendMsg";
                OkHttpUtil.postObject(jsonObject.toJSONString(), backUrl, handleSendMsgResponse());
                //添加到集合中，从而能在RecyclerView中显示
                chatMessages.add(new ChatMessage(chatMessage.getContactName(), chatMessage.getTime(), chatMessage.getContent(), true));

                //通知RecycllerView更新一行
                recyclerView.getAdapter().notifyItemRangeInserted(chatMessages.size() - 1, 1);
                //让RecyclerView向下滚动，以显示最新的消息
                recyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        });

        //聊天图标点击事件
        chatIconClickHandle();

    }

    //初始化聊天界面，添加电话，语音，表情等界面到聊天界面中，并隐藏它们
    public void initChatTools(){
        chatToolsFragment = new ArrayList<>();
        chatVoiceFragment = new ChatVoiceFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.chatToolsFragmentLayout, chatVoiceFragment);
        transaction.hide(chatVoiceFragment);
        chatVoiceFragment.setShow(false);
        chatToolsFragment.add(chatVoiceFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            //当点击动作栏上的返回图标时执行
            //关闭自己，返回来时的页面
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class ChatMessageAdapter extends
            RecyclerView.Adapter<ChatMessageAdapter.MyViewHolder> {

        private Context context;

        public ChatMessageAdapter(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ChatMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = getLayoutInflater().inflate(i, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatMessageAdapter.MyViewHolder myViewHolder, int i) {

            ChatMessage chatMessage = chatMessages.get(i);
            int position = getItemViewType(i);

            if (position == R.layout.chat_message_left_item) {
                myViewHolder.textViewReceive.setText(chatMessage.contactName + "：");
                myViewHolder.textViewLeft.setText(chatMessage.content);
            } else if (position == R.layout.chat_message_right_item) {
                myViewHolder.textViewRight.setText(chatMessage.content);
            }
        }

        @Override
        public int getItemCount() {
            return chatMessages.size();
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessage message = chatMessages.get(position);
            if (message.isMe) {
                //如果是我的，靠右显示
                return R.layout.chat_message_right_item;
            } else {
                //对方的，靠左显示
                return R.layout.chat_message_left_item;
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewLeft;
            private TextView textViewReceive;
            private ImageView imageViewLeft;
            private TextView textViewRight;
            private ImageView imageViewRight;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewLeft = itemView.findViewById(R.id.textViewLeft);
                textViewReceive = itemView.findViewById(R.id.textViewReceive);
                imageViewLeft = itemView.findViewById(R.id.imageViewLeft);
                textViewRight = itemView.findViewById(R.id.textViewRight);
                imageViewRight = itemView.findViewById(R.id.imageViewRight);
            }
        }
    }

    public void receiveMsg(ChatMessage msg) {
        chatMessages.add(msg);
    }

    public void flushChatView(RecyclerView recyclerView) {
        //通知RecycllerView更新一行
        recyclerView.getAdapter().notifyItemRangeInserted(chatMessages.size() - 1, 1);
        //让RecyclerView向下滚动，以显示最新的消息
        recyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    public Callback handleSendMsgResponse() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("wlf", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("wlf", response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.e("wlf", headers.name(i) + ":" + headers.value(i));
                }
                Log.e("wlf", "onResponse: " + response.body().string());
            }
        };
    }

    public void chatIconClickHandle() {
        iconVoiceHandle();
    }

    public void iconVoiceHandle() {

        chatIconVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFragment(chatVoiceFragment);
            }
        });
    }

    //显示或隐藏电话，语音，表情等功能的界面
    public void toggleFragment(ChatToolsFragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(R.anim.chat_tools_fragment_show, 0);
        if (fragment.isShow()){
            transaction.hide(fragment);
            fragment.setShow(false);
        }else{
            for (ChatToolsFragment item : chatToolsFragment){
                if (item.isShow()){
                    transaction.hide(item);
                    item.setShow(false);
                }
            }
            transaction.show(fragment);
            fragment.setShow(true);
        }
        transaction.commit();
    }

    //为显示和隐藏添加动画效果
    public void drawerAnimation(){

    }

    public void setLinearLayoutHeight(int height) {
        ViewGroup.LayoutParams lp;
        lp = chatTools.getLayoutParams();
        lp.height = height;
        chatTools.setLayoutParams(lp);
    }

    //软键盘处理函数，如果显示则隐藏，如果隐藏则显示
    private void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT
                    , InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //关闭指定文本输入框的软键盘
    private void closeKeyboard() {
        EditText editText = findViewById(R.id.editMessage);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}