package xar.com.qqapp;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import xar.com.qqapp.bean.User;
import xar.com.qqapp.utils.OkHttpUtil;
import xar.com.qqapp.utils.ToastUtil;
import xar.com.qqapp.utils.code.ErrorCodeUtil;
import xar.com.qqapp.utils.code.MsgCodeUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private ConstraintLayout layoutContext;
    private LinearLayout layoutHistory;
    private EditText editTextQQNum;
    private EditText editTextPassword;
    private Button loginButton;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        layoutContext = v.findViewById(R.id.layoutContext);
        layoutHistory = v.findViewById(R.id.layoutHistory);
        editTextQQNum = v.findViewById(R.id.editTextQQNum);
        editTextPassword = v.findViewById(R.id.editTextPassword);
        loginButton = v.findViewById(R.id.buttonLogin);

        v.findViewById(R.id.textViewHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutContext.setVisibility(v.INVISIBLE);
                layoutHistory.setVisibility(v.VISIBLE);
                //创建3条历史纪录菜单项，添加到layoutHistory中
                for (int i=0;i<3;i++){
                    View layoutItem = getActivity().getLayoutInflater().inflate(R.layout.login_history_item, null);
                    layoutItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editTextQQNum.setText("123456789");
                            layoutContext.setVisibility(View.VISIBLE);
                            layoutHistory.setVisibility(View.INVISIBLE);
                        }
                    });
                    layoutHistory.addView(layoutItem);
                }

                //使用动画显示历史记录
                AnimationSet set = (AnimationSet)AnimationUtils.loadAnimation(getContext(), R.anim.login_history_anim);
                layoutHistory.startAnimation(set);
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutHistory.getVisibility() == View.VISIBLE){
                    layoutContext.setVisibility(View.VISIBLE);
                    layoutHistory.setVisibility(View.INVISIBLE);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextQQNum.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                /*if (id.equals("") || password.equals("")){
                    Toast.makeText(v.getContext(), "用户名或密码不能为空！", Toast.LENGTH_LONG).show();
                    return ;
                }
                long userId = Long.parseLong(id);
                User user = new User(userId, password);
                JSONObject jsonObject = (JSONObject)JSONObject.toJSON(user);
                String obj = jsonObject.toJSONString();
                String backUrl = "login";
                OkHttpUtil.postObject(obj, backUrl, handleLogin(v));*/
                toMainActicity();

            }
        });

        return v;
    }

    public Callback handleLogin(View v){
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("wlf", "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String text = response.body().string();
                if (text.equals("" + ErrorCodeUtil.USERID_NOT_EXSIT)){
                    ToastUtil.toast(v, "该用户不存在！", Toast.LENGTH_LONG);
                }else if (text.equals("" + ErrorCodeUtil.USERID_OR_PASSWORD_NOT_EXSIT)){
                    ToastUtil.toast(v, "用户名或密码错误！", Toast.LENGTH_LONG);
                }else if (text.equals("" + MsgCodeUtil.SUCCESS)){
                    ToastUtil.toast(v, "登录成功！", Toast.LENGTH_SHORT);
                    toMainActicity();
                }else{
                    ToastUtil.toast(v, "未知异常！", Toast.LENGTH_LONG);
                }
            }
        };
    }

    public void toMainActicity(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();
    }
}
