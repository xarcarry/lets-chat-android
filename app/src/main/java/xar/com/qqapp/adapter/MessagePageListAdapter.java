package xar.com.qqapp.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xar.com.qqapp.R;

public class MessagePageListAdapter extends
        RecyclerView.Adapter<MessagePageListAdapter.MyViewHolder> {

    //用于获取
    private Activity activity;

    //创建一个带参数的构造方法，通过参数可以把Activity传过来
    public MessagePageListAdapter(Activity activity){
        this.activity = activity;
    }

    @NonNull
    @Override
    public MessagePageListAdapter.MyViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int viewType) {
        //从layout资源加载行view
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = null;
        if (viewType == R.layout.message_list_item_search){
            view = inflater.inflate(R.layout.message_list_item_search,
                    viewGroup, false);
        }else{
            view = inflater.inflate(R.layout.message_list_item_chat,
                    viewGroup, false);
        }
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagePageListAdapter.MyViewHolder myViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 15;
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position){
            //只有最顶端这行是搜索
            return R.layout.message_list_item_search;
        }
        //其余各行都一样的控件
        return R.layout.message_list_item_chat;
    }

    //将ViewHolder声明为Adapter的内部类
    class MyViewHolder extends RecyclerView.ViewHolder{
        public MyViewHolder(View itemView){
            super(itemView);
        }
    }
}
